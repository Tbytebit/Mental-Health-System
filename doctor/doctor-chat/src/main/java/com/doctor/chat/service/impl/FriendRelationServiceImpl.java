package com.doctor.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doctor.chat.entity.FriendRelation;
import com.doctor.chat.mapper.FriendRelationMapper;
import com.doctor.chat.service.FriendRelationService;
import com.doctor.chat.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * 好友关系Service实现
 */
@Service
public class FriendRelationServiceImpl extends ServiceImpl<FriendRelationMapper, FriendRelation> implements FriendRelationService {

    private static final Logger log = LoggerFactory.getLogger(FriendRelationServiceImpl.class);

    @Autowired
    private FriendRelationMapper friendRelationMapper;
    
    @Autowired
    private ChatMessageService chatMessageService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean addFriend(Long userId, Long friendId) {
        try {
            // 检查是否已经是好友
            if (checkFriendRelation(userId, friendId)) {
                log.info("已经是好友关系，无需重复添加: {} <-> {}", userId, friendId);
                return true;
            }
            
            // 先删除可能存在的单向关系
            friendRelationMapper.deleteFriendRelation(userId, friendId);
            
            Date now = new Date();
            
            // 添加正向关系
            FriendRelation relation1 = new FriendRelation();
            relation1.setUserId(userId);
            relation1.setFriendId(friendId);
            relation1.setStatus(0);
            relation1.setCreateTime(now);
            relation1.setUpdateTime(now);
            
            // 使用baseMapper.insert()而不是save()
            int insert1 = baseMapper.insert(relation1);
            
            if (insert1 <= 0) {
                throw new RuntimeException("添加正向好友关系失败");
            }
            
            // 添加反向关系
            FriendRelation relation2 = new FriendRelation();
            relation2.setUserId(friendId);
            relation2.setFriendId(userId);
            relation2.setStatus(0);
            relation2.setCreateTime(now);
            relation2.setUpdateTime(now);
            
            // 使用baseMapper.insert()而不是save()
            int insert2 = baseMapper.insert(relation2);
            
            if (insert2 <= 0) {
                throw new RuntimeException("添加反向好友关系失败");
            }
            
            log.info("成功建立双向好友关系: {} <-> {}", userId, friendId);
            
            return true;
        } catch (Exception e) {
            log.error("添加好友关系失败: " + e.getMessage(), e);
            throw new RuntimeException("添加好友关系失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Map<String, Object>> getFriendList(Long userId) {
        List<FriendRelation> friendList = friendRelationMapper.getFriendList(userId);
        List<Map<String, Object>> resultList = new ArrayList<>();
        
        for (FriendRelation relation : friendList) {
            Map<String, Object> map = new HashMap<>();
            map.put("relationId", relation.getRelationId());
            map.put("userId", relation.getUserId());
            map.put("friendId", relation.getFriendId());
            map.put("remark", relation.getRemark());
            map.put("status", relation.getStatus());
            map.put("createTime", relation.getCreateTime());
            
            // 从sys_user表获取好友的头像信息
            Map<String, Object> userInfo = friendRelationMapper.getUserInfoById(relation.getFriendId());
            if (userInfo != null) {
                map.put("nickname", userInfo.get("nick_name"));
                map.put("avatar", userInfo.get("avatar"));
            }
            
            resultList.add(map);
        }
        
        return resultList;
    }

    @Override
    public boolean checkFriendRelation(Long userId, Long friendId) {
        return friendRelationMapper.checkFriendRelation(userId, friendId) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteFriend(Long userId, Long friendId) {
        try {
            // 构建会话ID
            String conversationId;
            if (userId < friendId) {
                conversationId = userId + "_" + friendId;
            } else {
                conversationId = friendId + "_" + userId;
            }
            
            // 清空聊天记录
            boolean clearResult1 = chatMessageService.clearChatMessages(userId, friendId);
            // 同时也为好友清空聊天记录
            boolean clearResult2 = chatMessageService.clearChatMessages(friendId, userId);
            
            log.info("删除好友关系时已清空双方聊天记录: userId={}, friendId={}, conversationId={}, 清理结果: {}, {}", 
                    userId, friendId, conversationId, clearResult1, clearResult2);
            
            // 删除好友关系
            return friendRelationMapper.deleteFriendRelation(userId, friendId) > 0;
        } catch (Exception e) {
            log.error("删除好友关系失败: " + e.getMessage(), e);
            throw new RuntimeException("删除好友关系失败: " + e.getMessage(), e);
        }
    }

    @Override
    public boolean updateFriendRemark(Long userId, Long friendId, String remark) {
        try {
            Date updateTime = new Date();
            int result = friendRelationMapper.updateFriendRemark(userId, friendId, remark, updateTime);
            return result > 0;
        } catch (Exception e) {
            log.error("更新好友备注失败: " + e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public List<Map<String, Object>> searchUsersByNickname(String keyword, Long userId) {
        return friendRelationMapper.searchUsersByNickname(keyword, userId);
    }
} 