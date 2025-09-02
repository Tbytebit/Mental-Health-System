package com.doctor.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doctor.chat.entity.FriendRequest;
import com.doctor.chat.mapper.FriendRequestMapper;
import com.doctor.chat.service.FriendRelationService;
import com.doctor.chat.service.FriendRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 好友申请Service实现
 */
@Service
public class FriendRequestServiceImpl extends ServiceImpl<FriendRequestMapper, FriendRequest> implements FriendRequestService {

    private static final Logger log = LoggerFactory.getLogger(FriendRequestServiceImpl.class);

    @Autowired
    private FriendRequestMapper friendRequestMapper;
    
    @Autowired
    private FriendRelationService friendRelationService;
    /**
     * 检查是否存在待处理的好友申请
     *
     * @param fromUserId 发送者ID
     * @param toUserId 接收者ID
     * @return 是否存在
     */
    private boolean checkPendingRequest(Long fromUserId, Long toUserId) {
        try {
            // 安全检查
            if (fromUserId == null || toUserId == null) {
                log.warn("检查待处理好友申请参数无效: fromUserId={}, toUserId={}", fromUserId, toUserId);
                return false;
            }
            
            // 使用QueryWrapper构建查询条件
            QueryWrapper<FriendRequest> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("from_user_id", fromUserId.longValue())
                    .eq("to_user_id", toUserId.longValue())
                    .eq("status", 0); // 状态为待处理

            // 使用count方法而不是selectCount
            return count(queryWrapper) > 0;
        } catch (Exception e) {
            log.error("检查待处理好友申请失败: " + e.getMessage(), e);
            return false;
        }
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendFriendRequest(Long fromUserId, Long toUserId, String message, String remark) {
        try {
            // 安全检查
            if (fromUserId == null || toUserId == null) {
                log.warn("发送好友申请参数无效: fromUserId={}, toUserId={}", fromUserId, toUserId);
                return false;
            }
            
            // 检查是否已经是好友关系
            if (friendRelationService.checkFriendRelation(fromUserId, toUserId)) {
                log.info("已经是好友关系，无需发送申请: {} -> {}", fromUserId, toUserId);
                return true;
            }
            
            // 检查是否已经发送过申请且未处理
            if (checkPendingRequest(fromUserId, toUserId)) {
                log.info("已经存在待处理的好友申请: {} -> {}", fromUserId, toUserId);
                return true;
            }
            
            // 构建好友申请
            FriendRequest request = new FriendRequest();
            request.setFromUserId(fromUserId);
            request.setToUserId(toUserId);
            request.setMessage(message);
            request.setRemark(remark); // 设置备注
            request.setStatus(0); // 状态：0-待处理
            Date now = new Date();
            request.setCreateTime(now);
            request.setUpdateTime(now);
            
            // 记录一下实际保存的ID类型
            log.info("保存好友申请，fromUserId类型: {}, toUserId类型: {}", 
                     fromUserId.getClass().getName(), toUserId.getClass().getName());
            
            return save(request);
        } catch (Exception e) {
            log.error("发送好友申请失败: " + e.getMessage(), e);
            throw new RuntimeException("发送好友申请失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> getReceivedRequests(Long userId) {
        List<FriendRequest> requestList = friendRequestMapper.getReceivedRequests(userId);
        return convertToMap(requestList);
    }

    @Override
    public List<Map<String, Object>> getSentRequests(Long userId) {
        List<FriendRequest> requestList = friendRequestMapper.getSentRequests(userId);
        return convertToMap(requestList);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean acceptFriendRequest(Long requestId) {
        FriendRequest request = getById(requestId);
        if (request == null) {
            log.error("好友申请不存在: {}", requestId);
            return false;
        }
        
        if (request.getStatus() != 0) {
            log.error("好友申请已处理，无法重复操作: {}", requestId);
            return false;
        }
        
        // 更新申请状态为已接受
        request.setStatus(1);
        request.setUpdateTime(new Date());
        boolean updateResult = updateById(request);
        
        if (!updateResult) {
            log.error("更新好友申请状态失败: {}", requestId);
            throw new RuntimeException("更新好友申请状态失败");
        }
        
        // 添加好友关系
        try {
            // 检查是否有备注信息
            String remark = request.getRemark();
            boolean result = friendRelationService.addFriend(request.getToUserId(), request.getFromUserId());
            
            // 如果有备注信息且添加好友成功，则设置备注
            if (result && remark != null && !remark.isEmpty()) {
                friendRelationService.updateFriendRemark(request.getToUserId(), request.getFromUserId(), remark);
            }
            
            return result;
        } catch (Exception e) {
            log.error("建立好友关系失败: " + e.getMessage(), e);
            throw new RuntimeException("建立好友关系失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean rejectFriendRequest(Long requestId) {
        FriendRequest request = getById(requestId);
        if (request == null || request.getStatus() != 0) {
            return false;
        }
        
        // 更新申请状态为已拒绝
        request.setStatus(2);
        request.setUpdateTime(new Date());
        
        return updateById(request);
    }
    
    /**
     * 将实体列表转换为Map列表
     *
     * @param requestList 实体列表
     * @return Map列表
     */
    private List<Map<String, Object>> convertToMap(List<FriendRequest> requestList) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        for (FriendRequest request : requestList) {
            Map<String, Object> map = new HashMap<>();
            map.put("requestId", request.getRequestId());
            map.put("fromUserId", request.getFromUserId());
            map.put("toUserId", request.getToUserId());
            map.put("message", request.getMessage());
            map.put("status", request.getStatus());
            map.put("createTime", request.getCreateTime());
            map.put("updateTime", request.getUpdateTime());
            
            // TODO: 实际项目中需要根据用户ID查询用户信息补充申请者和接收者的姓名、头像等信息
            
            resultList.add(map);
        }
        
        return resultList;
    }
} 