package com.doctor.chat.controller;

import com.doctor.chat.dto.ChatMessageDTO;
import com.doctor.chat.entity.ChatMessage;
import com.doctor.chat.service.ChatMessageClearService;
import com.doctor.chat.service.ChatMessageService;
import com.doctor.common.core.domain.AjaxResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 聊天消息控制器
 */
@Slf4j
@RestController
@RequestMapping("/chat/message")
public class ChatMessageController {
    
    @Autowired
    private ChatMessageService chatMessageService;
    
    @Autowired
    private ChatMessageClearService chatMessageClearService;

    
    /**
     * 发送消息
     */
    @PostMapping("/send")
    public ResponseEntity<Map<String, Object>> sendMessage(@RequestBody ChatMessage chatMessage) {
        boolean result = chatMessageService.sendMessage(chatMessage);
        
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "发送成功");
            response.put("data", chatMessage);
        } else {
            response.put("code", 500);
            response.put("message", "发送失败");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取与指定用户的聊天记录
     */

    /*
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getChatHistory(
            @RequestParam("userId1") Long userId1,
            @RequestParam("userId2") Long userId2,
            @RequestParam(value = "limit", defaultValue = "50") Integer limit
    ) {
        List<ChatMessageDTO> chatHistory = chatMessageService.getChatHistory(userId1, userId2, limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", chatHistory);
        
        return ResponseEntity.ok(response);
    }
    */
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getChatHistory(
            @RequestParam("userId1") Long userId1,
            @RequestParam("userId2") Long userId2,
            @RequestParam(value = "requesterId", required = false) Long requesterId,
            @RequestParam(value = "limit", defaultValue = "50") Integer limit
    ) {
        // 如果没有指定请求者ID，默认使用userId1
        if (requesterId == null) {
            requesterId = userId1;
        }
        List<ChatMessageDTO> chatHistory = chatMessageService.getChatHistory(userId1, userId2, requesterId, limit);

        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", chatHistory);

        return ResponseEntity.ok(response);
    }
    /**
     * 获取会话的聊天记录
     */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Map<String, Object>> getMessagesByConversationId(
            @PathVariable("conversationId") String conversationId,
            @RequestParam(value = "limit", defaultValue = "50") Integer limit
    ) {
        List<ChatMessageDTO> messages = chatMessageService.getMessagesByConversationId(conversationId, limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", messages);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@RequestParam("receiverId") Long receiverId) {
        int unreadCount = chatMessageService.getUnreadCount(receiverId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", unreadCount);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 标记消息为已读
     */
    @PostMapping("/read")
    public ResponseEntity<Map<String, Object>> markMessageRead(@RequestBody Map<String, Object> params) {
        Map<String, Object> response = new HashMap<>();
        try {
            log.info("收到标记消息已读请求，参数: {}", params);
            
            // 参数验证
            if (params == null || !params.containsKey("senderId") || !params.containsKey("receiverId")) {
                log.error("标记消息已读失败: 缺少必要参数");
                response.put("code", 400);
                response.put("message", "缺少必要参数");
                return ResponseEntity.ok(response);
            }
            
            // 类型转换
            Long senderId = null;
            Long receiverId = null;
            try {
                Object senderIdObj = params.get("senderId");
                Object receiverIdObj = params.get("receiverId");
                
                if (senderIdObj != null) {
                    senderId = Long.valueOf(senderIdObj.toString());
                }
                
                if (receiverIdObj != null) {
                    receiverId = Long.valueOf(receiverIdObj.toString());
                }
            } catch (NumberFormatException e) {
                log.error("标记消息已读失败: 参数类型错误", e);
                response.put("code", 400);
                response.put("message", "参数类型错误");
                return ResponseEntity.ok(response);
            }
            
            // 检查参数值是否有效
            if (senderId == null || receiverId == null) {
                log.error("标记消息已读失败: 参数不能为空");
                response.put("code", 400);
                response.put("message", "参数不能为空");
                return ResponseEntity.ok(response);
            }
            
            // 调用服务
            boolean result = chatMessageService.markMessageRead(senderId, receiverId);
            
            if (result) {
                response.put("code", 200);
                response.put("message", "标记成功");
            } else {
                response.put("code", 500);
                response.put("message", "标记失败");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("标记消息已读失败: {}", e.getMessage(), e);
            response.put("code", 500);
            response.put("message", "标记失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }
    
    /**
     * 清空聊天记录
     */
    @DeleteMapping("/clear/{userId}/{friendId}")
    public AjaxResult clearChatMessages(@PathVariable Long userId, @PathVariable Long friendId) {
        try {
            // 参数验证
            if (userId == null || friendId == null) {
                return AjaxResult.error("参数不能为空");
            }

            if (userId.equals(friendId)) {
                return AjaxResult.error("不能清空与自己的聊天记录");
            }

            // 记录清空历史
            String conversationId = userId < friendId ? userId + "_" + friendId : friendId + "_" + userId;
            boolean result = chatMessageClearService.recordClearHistory(userId, friendId, conversationId);

            if (result) {
                log.info("用户{}清空与用户{}的聊天记录成功", userId, friendId);
                return AjaxResult.success("清空成功");
            } else {
                log.error("用户{}清空与用户{}的聊天记录失败", userId, friendId);
                return AjaxResult.error("清空失败");
            }
        } catch (Exception e) {
            log.error("清空聊天记录异常: userId={}, friendId={}, error={}", userId, friendId, e.getMessage(), e);
            return AjaxResult.error("系统异常，请稍后重试");
        }
    }
    
    /**
     * 获取上次清空聊天记录的时间
     */
    @GetMapping("/clear/time/{userId}/{friendId}")
    public ResponseEntity<Map<String, Object>> getLastClearTime(
            @PathVariable("userId") Long userId,
            @PathVariable("friendId") Long friendId
    ) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            log.info("获取上次清空聊天记录时间，参数: userId={}, friendId={}", userId, friendId);
            
            // 参数验证
            if (userId == null || friendId == null) {
                log.error("获取上次清空聊天记录时间失败: 缺少必要参数");
                response.put("code", 400);
                response.put("message", "缺少必要参数");
                return ResponseEntity.ok(response);
            }
            
            // 获取清空时间
            Date clearTime = chatMessageClearService.getLastClearTime(userId, friendId);
            
            response.put("code", 200);
            response.put("message", "获取成功");
            response.put("data", clearTime);
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("获取上次清空聊天记录时间失败: {}", e.getMessage(), e);
            response.put("code", 500);
            response.put("message", "获取失败: " + e.getMessage());
            return ResponseEntity.ok(response);
        }
    }

}