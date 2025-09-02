package com.doctor.chat.controller;

import com.doctor.chat.entity.ChatUser;
import com.doctor.chat.service.ChatMessageService;
import com.doctor.chat.service.ChatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 聊天用户控制器
 */
@RestController
@RequestMapping("/chat/user")
public class ChatUserController {
    
    @Autowired
    private ChatUserService chatUserService;
    
    @Autowired
    private ChatMessageService chatMessageService;
    
    @Autowired
    @Qualifier("chatRedisTemplate")
    private RedisTemplate<String, Object> redisTemplate;
    
    // Redis用户在线状态的key前缀
    private static final String REDIS_ONLINE_KEY_PREFIX = "chat:user:online:";
    
    /**
     * 获取用户在线状态
     */
    @GetMapping("/status/{userId}")
    public ResponseEntity<Map<String, Object>> getUserStatus(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (userId == null) {
                result.put("code", 400);
                result.put("msg", "用户ID不能为空");
                result.put("data", "0");
                return ResponseEntity.ok(result);
            }
            
            String status = chatUserService.getUserStatus(userId);
            
            result.put("code", 200);
            result.put("msg", "success");
            result.put("data", status);
        } catch (Exception e) {
            result.put("code", 200); // 仍返回200但使用默认值
            result.put("msg", "获取用户状态失败，使用默认值");
            result.put("data", "0"); // 出错时默认离线
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 更新用户在线状态（通过HTTP心跳）- 已迁移到UserStatusController
     * 此方法保留用于兼容旧版客户端，路径改为update-legacy
     */
    @PostMapping("/status/update-legacy")
    public ResponseEntity<Map<String, Object>> updateUserStatus(@RequestBody Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取用户ID
            Object userIdObj = params.get("userId");
            if (userIdObj == null) {
                result.put("code", 400);
                result.put("msg", "用户ID不能为空");
                return ResponseEntity.ok(result);
            }
            
            Long userId;
            try {
                userId = Long.valueOf(userIdObj.toString());
            } catch (NumberFormatException e) {
                result.put("code", 400);
                result.put("msg", "无效的用户ID格式");
                return ResponseEntity.ok(result);
            }
            
            // 更新Redis中的在线状态
            long currentTime = System.currentTimeMillis();
            redisTemplate.opsForValue().set(
                REDIS_ONLINE_KEY_PREFIX + userId, 
                currentTime, 
                2, 
                TimeUnit.MINUTES
            );
            
            // 更新数据库中的用户状态
            boolean success = chatUserService.updateUserStatus(userId, "1");
            
            if (success) {
                result.put("code", 200);
                result.put("msg", "用户状态更新成功");
                result.put("success", true);
            } else {
                result.put("code", 500);
                result.put("msg", "用户状态更新失败");
                result.put("success", false);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", "更新用户状态时发生错误: " + e.getMessage());
            result.put("success", false);
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取用户未读消息数
     */
    @GetMapping("/unread/{userId}")
    public ResponseEntity<Map<String, Object>> getUnreadMessageCount(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 调用消息服务获取未读消息数
            int unreadCount = chatMessageService.countUnreadMessages(userId);
            
            result.put("code", 200);
            result.put("msg", "success");
            result.put("data", unreadCount);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", e.getMessage());
            result.put("data", 0); // 默认值
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取特定会话的未读消息数
     */
    @GetMapping("/unread/{userId}/{friendId}")
    public ResponseEntity<Map<String, Object>> getUnreadMessageCountByFriend(
            @PathVariable Long userId, 
            @PathVariable Long friendId) {
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 构建会话ID
            String conversationId;
            if (userId < friendId) {
                conversationId = userId + "_" + friendId;
            } else {
                conversationId = friendId + "_" + userId;
            }
            
            // 获取未读消息数
            int unreadCount = chatMessageService.countUnreadMessagesByConversation(userId, conversationId);
            
            result.put("code", 200);
            result.put("msg", "success");
            result.put("data", unreadCount);
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", e.getMessage());
            result.put("data", 0); // 默认值
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 注册/创建用户
     */
    @PostMapping("/register")
    public ResponseEntity<Map<String, Object>> registerUser(@RequestBody ChatUser user) {
        // 设置初始状态为离线
        user.setStatus("0");
        boolean success = chatUserService.saveUser(user);
        
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("code", 200);
            result.put("msg", "用户注册成功");
            result.put("data", user.getUserId());
        } else {
            result.put("code", 500);
            result.put("msg", "用户注册失败");
        }
        
        return ResponseEntity.ok(result);
    }
    
    /**
     * 获取用户信息
     */
    @GetMapping("/info/{userId}")
    public ResponseEntity<Map<String, Object>> getUserInfo(@PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 调用服务获取用户信息
            Map<String, Object> userInfo = chatUserService.getUserInfoFromSysUser(userId);
            
            if (userInfo != null) {
                result.put("code", 200);
                result.put("msg", "success");
                result.put("data", userInfo);
            } else {
                result.put("code", 404);
                result.put("msg", "用户不存在");
                result.put("data", null);
            }
        } catch (Exception e) {
            result.put("code", 500);
            result.put("msg", e.getMessage());
            result.put("data", null);
        }
        
        return ResponseEntity.ok(result);
    }
} 