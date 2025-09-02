package com.doctor.chat.service.impl;

import com.doctor.chat.entity.ChatUser;
import com.doctor.chat.service.ChatUserService;
import com.doctor.chat.mapper.UserInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * 聊天用户服务实现类 - 完全使用Redis实现，不再依赖chat_user表
 */
@Slf4j
@Service
public class ChatUserServiceImpl implements ChatUserService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @Autowired
    private UserInfoMapper userInfoMapper;
    
    private static final String REDIS_ONLINE_KEY_PREFIX = "chat:user:online:";
    private static final String REDIS_HEARTBEAT_KEY_PREFIX = "chat:user:heartbeat:";
    private static final String REDIS_USER_INFO_PREFIX = "chat:user:info:";
    private static final int REDIS_STATUS_EXPIRE_TIME = 120; // 状态过期时间（秒）

    @Override
    public boolean updateUserStatus(Long userId, String status) {
        if (userId == null) {
            return false;
        }
        
        try {
            // 更新Redis缓存
            String redisKey = REDIS_ONLINE_KEY_PREFIX + userId;
            redisTemplate.opsForValue().set(redisKey, status, REDIS_STATUS_EXPIRE_TIME, TimeUnit.SECONDS);
            
            if ("1".equals(status)) {
                // 在线状态同时更新心跳时间
                redisTemplate.opsForValue().set(REDIS_HEARTBEAT_KEY_PREFIX + userId, 
                        System.currentTimeMillis(), REDIS_STATUS_EXPIRE_TIME, TimeUnit.SECONDS);
            }
            
            log.info("用户{}状态更新为{}", userId, status);
            return true;
        } catch (Exception e) {
            log.error("更新用户状态失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public String getUserStatus(Long userId) {
        if (userId == null) {
            return "0";  // 默认离线
        }
        
        try {
            // 从Redis获取状态
            String redisKey = REDIS_ONLINE_KEY_PREFIX + userId;
            Object status = redisTemplate.opsForValue().get(redisKey);
            
            if (status != null) {
                return status.toString();
            }
            
            return "0";  // Redis中不存在，默认离线
        } catch (Exception e) {
            log.error("获取用户状态失败: {}", e.getMessage(), e);
            return "0";  // 出错时默认返回离线
        }
    }

    @Override
    public Map<String, String> getBatchUserStatus(List<Long> userIds) {
        Map<String, String> result = new HashMap<>();
        
        if (userIds == null || userIds.isEmpty()) {
            return result;
        }
        
        try {
            // 遍历用户ID
            for (Long userId : userIds) {
                String status = getUserStatus(userId);
                result.put(String.valueOf(userId), status);
            }
        } catch (Exception e) {
            log.error("批量获取用户状态失败: {}", e.getMessage(), e);
        }
        
        return result;
    }

    @Override
    public int cleanTimeoutUsers(long timeoutMillis) {
        long currentTime = System.currentTimeMillis();
        int count = 0;
        
        try {
            // 获取所有心跳记录
            Set<String> keys = redisTemplate.keys(REDIS_HEARTBEAT_KEY_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                for (String key : keys) {
                    String userId = key.substring(REDIS_HEARTBEAT_KEY_PREFIX.length());
                    Object heartbeatObj = redisTemplate.opsForValue().get(key);
                    
                    if (heartbeatObj != null) {
                        try {
                            Long heartbeatTime = Long.parseLong(heartbeatObj.toString());
                            long timeDiff = currentTime - heartbeatTime;
                            
                            // 如果超过超时时间，标记为离线
                            if (timeDiff > timeoutMillis) {
                                // 更新用户状态为离线
                                String statusKey = REDIS_ONLINE_KEY_PREFIX + userId;
                                redisTemplate.opsForValue().set(statusKey, "0", REDIS_STATUS_EXPIRE_TIME, TimeUnit.SECONDS);
                                
                                count++;
                                log.info("用户{}因超时{}ms被标记为离线", userId, timeDiff);
                            }
                        } catch (NumberFormatException e) {
                            log.warn("无效的心跳时间格式: {}", heartbeatObj);
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("清理超时用户失败: {}", e.getMessage(), e);
        }
        
        return count;
    }

    @Override
    public boolean saveUser(ChatUser user) {
        if (user == null || user.getUserId() == null) {
            return false;
        }
        
        try {
            // 将用户状态存储到Redis中
            String statusKey = REDIS_ONLINE_KEY_PREFIX + user.getUserId();
            redisTemplate.opsForValue().set(statusKey, user.getStatus(), REDIS_STATUS_EXPIRE_TIME, TimeUnit.SECONDS);
            
            // 存储用户的其他信息到Redis的hash结构中
            String userKey = REDIS_USER_INFO_PREFIX + user.getUserId();
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("userId", user.getUserId());
            userMap.put("userName", user.getUserName());
            userMap.put("nickName", user.getNickName());
            userMap.put("status", user.getStatus());
            redisTemplate.opsForHash().putAll(userKey, userMap);
            
            // 设置较长的过期时间，因为这是用户基本信息
            redisTemplate.expire(userKey, 7, TimeUnit.DAYS);
            
            log.info("用户{}信息保存成功", user.getUserId());
            return true;
        } catch (Exception e) {
            log.error("保存用户信息失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public Map<String, Object> getUserInfoFromSysUser(Long userId) {
        if (userId == null) {
            return null;
        }
        
        try {
            // 先尝试从缓存获取
            String cacheKey = "chat:user:sysInfo:" + userId;
            Object cachedInfo = redisTemplate.opsForValue().get(cacheKey);
            
            if (cachedInfo != null && cachedInfo instanceof Map) {
                return (Map<String, Object>) cachedInfo;
            }
            
            // 缓存中没有，从数据库获取
            Map<String, Object> userInfo = userInfoMapper.getUserInfoById(userId);
            
            if (userInfo != null) {
                // 缓存用户信息，设置30分钟过期
                redisTemplate.opsForValue().set(cacheKey, userInfo, 30, TimeUnit.MINUTES);
            }
            
            return userInfo;
        } catch (Exception e) {
            log.error("获取用户信息失败: {}", e.getMessage(), e);
            return null;
        }
    }
} 