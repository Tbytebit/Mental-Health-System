package com.doctor.web.task;

import com.doctor.common.constant.CacheConstants;
import com.doctor.common.constant.Constants;
import com.doctor.common.core.domain.model.LoginUser;
import com.doctor.common.core.redis.RedisCache;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * 好友关系删除器
 * 负责在医生就诊后调用聊天模块接口删除好友关系
 */
@Component
public class FriendRequestRemover {
    
    private static final Logger logger = LoggerFactory.getLogger(FriendRequestRemover.class);
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private RedisCache redisCache;
    
    @Value("${doctor.chat.delete-friend-url:http://localhost:6680/chat/friend/delete}")
    private String deleteFriendUrl;
    
    @Value("${token.header:Authorization}")
    private String tokenHeader;
    
    @Value("${token.secret:abcdefghijklmnopqrstuvwxyz}")
    private String tokenSecret;
    
    /**
     * 删除好友关系
     * 通过HTTP请求调用聊天模块的接口
     *
     * @param patientId 患者ID
     * @param doctorId 医生ID
     * @return 是否删除成功
     */
    public boolean removeFriendRelation(Long patientId, Long doctorId) {
        try {
            // 检查参数
            if (patientId == null || doctorId == null) {
                logger.error("删除好友关系失败：用户ID不能为空 patientId={}, doctorId={}", patientId, doctorId);
                return false;
            }
            
            // 设置HTTP请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
            
            // 获取当前登录用户的Token（如果有）
            String token = getCurrentUserToken(patientId);
            if (token != null && !token.isEmpty() && token.contains(".")) {
                logger.debug("添加认证Token到请求头: {}", tokenHeader);
                // 检查token是否已经包含Bearer前缀
                if (!token.startsWith("Bearer ")) {
                    headers.set(tokenHeader, "Bearer " + token);
                } else {
                    headers.set(tokenHeader, token);
                }
            } else {
                logger.warn("未找到有效的用户Token或Token格式不正确，请求可能会被拒绝");
            }
            
            // 准备表单请求参数 - 使用MultiValueMap而非HashMap
            MultiValueMap<String, String> formParams = new LinkedMultiValueMap<>();
            formParams.add("userId", patientId.toString());
            formParams.add("friendId", doctorId.toString());
            
            // 创建HTTP请求
            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formParams, headers);
            
            logger.info("删除好友关系请求: 患者ID={}, 医生ID={}, URL={}", 
                patientId, doctorId, deleteFriendUrl);
            logger.debug("请求参数: {}", formParams);
            
            // 执行请求
            ResponseEntity<Map> response;
            try {
                response = restTemplate.exchange(
                    deleteFriendUrl, 
                    HttpMethod.POST,
                    entity,
                    Map.class
                );
            } catch (RestClientException e) {
                logger.error("好友关系删除请求失败，可能是网络问题或服务不可用: {}", e.getMessage(), e);
                return false;
            }
            
            // 检查响应状态
            if (response != null && response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                Map<String, Object> responseBody = response.getBody();
                logger.debug("好友关系删除响应: {}", responseBody);
                
                // 获取响应码，确保非null
                Integer code = responseBody.get("code") instanceof Number ?
                    ((Number) responseBody.get("code")).intValue() : null;
                
                if (code != null && code == 200) {
                    logger.info("好友关系删除成功");
                    return true;
                } else {
                    // 尝试获取错误消息
                    String message = null;
                    if (responseBody.containsKey("message")) {
                        message = responseBody.get("message").toString();
                    } else if (responseBody.containsKey("msg")) {
                        message = responseBody.get("msg").toString();
                    } else {
                        message = "未知错误";
                    }
                    
                    logger.error("好友关系删除失败: {}, 响应码: {}", message, code);
                }
            } else {
                logger.error("好友关系删除请求失败，HTTP状态码: {}, 响应体: {}", 
                    response != null ? response.getStatusCodeValue() : "未获取到响应",
                    response != null ? response.getBody() : "无响应体");
            }
        } catch (Exception e) {
            logger.error("删除好友关系时发生异常: {}", e.getMessage(), e);
        }
        
        return false;
    }
    
    /**
     * 获取当前用户的Token
     * 注意：由于是定时任务调用，无法从当前请求获取Token，需要通过其他方式获取指定用户的Token
     * 
     * @param userId 用户ID
     * @return 用户Token，如果不存在则返回null
     */
    private String getCurrentUserToken(Long userId) {
        try {
            // 检查参数
            if (userId == null) {
                logger.warn("获取用户Token失败：用户ID为空");
                return null;
            }
            
            // 查找所有登录token
            Collection<String> keys = redisCache.keys(CacheConstants.LOGIN_TOKEN_KEY + "*");
            if (keys == null || keys.isEmpty()) {
                logger.warn("Redis中未找到任何登录用户信息");
                return null;
            }
            
            // 遍历所有token，查找匹配用户ID的登录信息
            for (String key : keys) {
                LoginUser loginUser = redisCache.getCacheObject(key);
                if (loginUser != null && loginUser.getUserId() != null) {
                    // 确保ID类型一致，将可能的Integer类型转换为Long进行比较
                    Long loginUserId = loginUser.getUserId();
                    if (userId.equals(loginUserId)) {
                        // 生成JWT token
                        Map<String, Object> claims = new HashMap<>();
                        claims.put(Constants.LOGIN_USER_KEY, loginUser.getToken());
                        
                        String token = Jwts.builder()
                                .setClaims(claims)
                                .signWith(SignatureAlgorithm.HS512, tokenSecret)
                                .compact();
                        
                        logger.info("成功获取用户 {} 的Token", userId);
                        return token;
                    }
                }
            }
            
            logger.warn("未在Redis中找到用户 {} 的登录信息", userId);
            return null;
        } catch (Exception e) {
            logger.error("获取用户Token失败: {}", e.getMessage(), e);
            return null;
        }
    }
} 