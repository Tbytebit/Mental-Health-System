package com.doctor.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.doctor.chat.entity.FriendRequest;

import java.util.List;
import java.util.Map;

/**
 * 好友请求服务接口
 */
public interface FriendRequestService extends IService<FriendRequest> {
    
    /**
     * 发送好友申请
     *
     * @param fromUserId 申请者ID
     * @param toUserId   接收者ID
     * @param message    申请消息
     * @param remark     好友备注
     * @return 是否成功
     */
    boolean sendFriendRequest(Long fromUserId, Long toUserId, String message, String remark);
    
    /**
     * 获取收到的好友申请列表
     *
     * @param userId 用户ID
     * @return 申请列表
     */
    List<Map<String, Object>> getReceivedRequests(Long userId);
    
    /**
     * 获取发送的好友申请列表
     *
     * @param userId 用户ID
     * @return 申请列表
     */
    List<Map<String, Object>> getSentRequests(Long userId);
    
    /**
     * 接受好友申请
     *
     * @param requestId 申请ID
     * @return 是否成功
     */
    boolean acceptFriendRequest(Long requestId);
    
    /**
     * 拒绝好友申请
     *
     * @param requestId 申请ID
     * @return 是否成功
     */
    boolean rejectFriendRequest(Long requestId);
} 