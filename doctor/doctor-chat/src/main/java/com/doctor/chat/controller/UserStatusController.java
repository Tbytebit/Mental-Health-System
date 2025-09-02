package com.doctor.chat.controller;

import com.doctor.chat.common.R;
import com.doctor.chat.service.ChatUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户状态控制器
 */
@RestController
@RequestMapping("/chat/user/v2")
public class UserStatusController {
    
    @Autowired
    private ChatUserService chatUserService;
    
    /**
     * 获取用户在线状态
     * 
     * @param userId 用户ID
     * @return 在线状态（1=在线，0=离线）
     */
    @GetMapping("/status/{userId}")
    public R<String> getUserStatus(@PathVariable Long userId) {
        try {
            String status = chatUserService.getUserStatus(userId);
            return R.ok(status);
        } catch (Exception e) {
            return R.error("获取用户状态失败");
        }
    }
    
    /**
     * 批量获取用户在线状态
     * 
     * @param userIds 用户ID列表
     * @return 用户状态Map
     */
    @PostMapping("/status/batch")
    public R<Map<String, String>> getBatchUserStatus(@RequestBody Map<String, List<Long>> body) {
        try {
            List<Long> userIds = body.get("userIds");
            if (userIds == null || userIds.isEmpty()) {
                return R.error("用户ID列表不能为空");
            }
            
            Map<String, String> statusMap = chatUserService.getBatchUserStatus(userIds);
            return R.ok(statusMap);
        } catch (Exception e) {
            return R.error("批量获取用户状态失败");
        }
    }
    
    /**
     * 更新用户在线状态（心跳）
     * 
     * @param body 包含userId的请求体
     * @return 成功/失败
     */
    @PostMapping("/status/update")
    public R<Boolean> updateUserStatus(@RequestBody Map<String, Object> body) {
        try {
            Object userIdObj = body.get("userId");
            if (userIdObj == null) {
                return R.error("用户ID不能为空");
            }
            
            Long userId;
            try {
                userId = Long.parseLong(userIdObj.toString());
            } catch (NumberFormatException e) {
                return R.error("无效的用户ID");
            }
            
            boolean result = chatUserService.updateUserStatus(userId, "1");
            return result ? R.ok(true) : R.error("更新用户状态失败");
        } catch (Exception e) {
            return R.error("更新用户状态失败: " + e.getMessage());
        }
    }
} 