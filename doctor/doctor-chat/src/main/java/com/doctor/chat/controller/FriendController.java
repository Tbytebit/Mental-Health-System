package com.doctor.chat.controller;

import com.doctor.chat.entity.FriendRequest;
import com.doctor.chat.service.FriendRelationService;
import com.doctor.chat.service.FriendRequestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 好友关系和好友申请控制器
 */
@RestController
@RequestMapping("/chat/friend")
public class FriendController {
    
    private static final Logger log = LoggerFactory.getLogger(FriendController.class);
    
    @Autowired
    private FriendRelationService friendRelationService;
    
    @Autowired
    private FriendRequestService friendRequestService;
    
    /**
     * 获取好友列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getFriendList(@RequestParam("userId") Long userId) {
        List<Map<String, Object>> friendList = friendRelationService.getFriendList(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", friendList);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 添加好友关系（直接添加，不需要申请）
     */
    @PostMapping("/add")
    public ResponseEntity<Map<String, Object>> addFriend(
            @RequestParam("userId") Long userId,
            @RequestParam("friendId") Long friendId
    ) {
        boolean result = friendRelationService.addFriend(userId, friendId);
        
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "添加成功");
        } else {
            response.put("code", 500);
            response.put("message", "添加失败");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 删除好友关系
     */
    @DeleteMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteFriend(
            @RequestParam("userId") Long userId,
            @RequestParam("friendId") Long friendId
    ) {
        boolean result = friendRelationService.deleteFriend(userId, friendId);
        
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "删除成功");
        } else {
            response.put("code", 500);
            response.put("message", "删除失败");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 删除好友关系 (POST方法)
     */
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteFriendPost(
            @RequestParam("userId") Long userId,
            @RequestParam("friendId") Long friendId
    ) {
        boolean result = friendRelationService.deleteFriend(userId, friendId);
        
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "删除成功");
        } else {
            response.put("code", 500);
            response.put("message", "删除失败");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 修改好友备注
     */
    @PutMapping("/remark")
    public ResponseEntity<Map<String, Object>> updateFriendRemark(
            @RequestBody Map<String, Object> requestBody
    ) {
        Long userId = null;
        Long friendId = null;
        String remark = null;
        
        if (requestBody.containsKey("userId")) {
            userId = Long.valueOf(requestBody.get("userId").toString());
        }
        
        if (requestBody.containsKey("friendId")) {
            friendId = Long.valueOf(requestBody.get("friendId").toString());
        }
        
        if (requestBody.containsKey("remark")) {
            remark = requestBody.get("remark").toString();
        }
        
        // 参数校验
        if (userId == null || friendId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", "必须提供用户ID和好友ID");
            return ResponseEntity.ok(response);
        }
        
        boolean result = friendRelationService.updateFriendRemark(userId, friendId, remark);
        
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "修改成功");
        } else {
            response.put("code", 500);
            response.put("message", "修改失败");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 发送好友申请
     */
    @PostMapping("/request/send")
    public ResponseEntity<Map<String, Object>> sendFriendRequest(
            @RequestBody Map<String, Object> requestBody
    ) {
        // 从请求体中获取参数
        Long fromUserId = null;
        Long toUserId = null;
        String message = null;
        String remark = null;
        
        if (requestBody.containsKey("fromUserId")) {
            Object rawFromUserId = requestBody.get("fromUserId");
            if (rawFromUserId instanceof Integer) {
                fromUserId = ((Integer) rawFromUserId).longValue();
            } else if (rawFromUserId instanceof Long) {
                fromUserId = (Long) rawFromUserId;
            } else if (rawFromUserId != null) {
                try {
                    fromUserId = Long.valueOf(rawFromUserId.toString());
                } catch (NumberFormatException e) {
                    log.error("无法将fromUserId转换为Long类型: {}", rawFromUserId);
                }
            }
        }
        
        if (requestBody.containsKey("toUserId")) {
            Object rawToUserId = requestBody.get("toUserId");
            if (rawToUserId instanceof Integer) {
                toUserId = ((Integer) rawToUserId).longValue();
            } else if (rawToUserId instanceof Long) {
                toUserId = (Long) rawToUserId;
            } else if (rawToUserId != null) {
                try {
                    toUserId = Long.valueOf(rawToUserId.toString());
                } catch (NumberFormatException e) {
                    log.error("无法将toUserId转换为Long类型: {}", rawToUserId);
                }
            }
        }
        
        if (requestBody.containsKey("message")) {
            message = requestBody.get("message").toString();
        }
        
        if (requestBody.containsKey("remark")) {
            remark = requestBody.get("remark").toString();
        }
        
        // 参数校验
        if (fromUserId == null || toUserId == null) {
            Map<String, Object> response = new HashMap<>();
            response.put("code", 400);
            response.put("message", "必须提供发送者ID和接收者ID");
            return ResponseEntity.ok(response);
        }
        
        boolean result = friendRequestService.sendFriendRequest(fromUserId, toUserId, message, remark);
        
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "发送成功");
        } else {
            response.put("code", 500);
            response.put("message", "发送失败，可能已经是好友或已发送过申请");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取收到的好友申请列表
     */
    @GetMapping("/request/received")
    public ResponseEntity<Map<String, Object>> getReceivedRequests(@RequestParam("userId") Long userId) {
        List<Map<String, Object>> requestList = friendRequestService.getReceivedRequests(userId);
        
        // 处理用户信息，确保隐私保护
        for (Map<String, Object> request : requestList) {
            // 移除敏感信息，如电话号码等
            if (request.containsKey("phone")) {
                request.remove("phone");
            }
            if (request.containsKey("email")) {
                request.remove("email");
            }
            
            // 确保昵称或备注的存在，不直接显示用户ID
            if (!request.containsKey("nickname") || request.get("nickname") == null) {
                request.put("nickname", "用户");
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", requestList);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取发送的好友申请列表
     */
    @GetMapping("/request/sent")
    public ResponseEntity<Map<String, Object>> getSentRequests(@RequestParam("userId") Long userId) {
        List<Map<String, Object>> requestList = friendRequestService.getSentRequests(userId);
        
        // 处理用户信息，确保隐私保护
        for (Map<String, Object> request : requestList) {
            // 移除敏感信息，如电话号码等
            if (request.containsKey("phone")) {
                request.remove("phone");
            }
            if (request.containsKey("email")) {
                request.remove("email");
            }
            
            // 确保昵称或备注的存在，不直接显示用户ID
            if (!request.containsKey("nickname") || request.get("nickname") == null) {
                request.put("nickname", "用户");
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", requestList);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 接受好友申请
     */
    @PostMapping("/request/accept")
    public ResponseEntity<Map<String, Object>> acceptFriendRequest(
            @RequestParam("requestId") Long requestId
    ) {
        boolean result = friendRequestService.acceptFriendRequest(requestId);
        
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "接受成功");
        } else {
            response.put("code", 500);
            response.put("message", "接受失败");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 拒绝好友申请
     */
    @PostMapping("/request/reject")
    public ResponseEntity<Map<String, Object>> rejectFriendRequest(@RequestParam("requestId") Long requestId) {
        boolean result = friendRequestService.rejectFriendRequest(requestId);
        
        Map<String, Object> response = new HashMap<>();
        if (result) {
            response.put("code", 200);
            response.put("message", "拒绝成功");
        } else {
            response.put("code", 500);
            response.put("message", "拒绝失败");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 搜索用户
     */
    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchUsers(
            @RequestParam("keyword") String keyword,
            @RequestParam("userId") Long userId
    ) {
        List<Map<String, Object>> userList = friendRelationService.searchUsers(keyword, userId);
        
        // 处理用户信息，确保隐私保护
        for (Map<String, Object> user : userList) {
            // 移除敏感信息，如电话号码等
            if (user.containsKey("phone")) {
                user.remove("phone");
            }
            if (user.containsKey("email")) {
                user.remove("email");
            }
            
            // 确保昵称的存在，不直接显示用户ID
            if (!user.containsKey("nickname") || user.get("nickname") == null) {
                user.put("nickname", "用户");
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "获取成功");
        response.put("data", userList);
        
        return ResponseEntity.ok(response);
    }
} 