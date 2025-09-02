package com.doctor.chat.service;

import com.doctor.chat.entity.FriendRelation;

import java.util.List;
import java.util.Map;

/**
 * 好友关系Service接口
 */
public interface FriendRelationService {
    
    /**
     * 添加好友关系（双向添加）
     *
     * @param userId   用户ID
     * @param friendId 好友ID
     * @return 是否成功
     */
    boolean addFriend(Long userId, Long friendId);
    
    /**
     * 获取用户的好友列表
     *
     * @param userId 用户ID
     * @return 好友信息列表
     */
    List<Map<String, Object>> getFriendList(Long userId);
    
    /**
     * 检查是否已经是好友关系
     *
     * @param userId   用户ID
     * @param friendId 好友ID
     * @return 是否是好友
     */
    boolean checkFriendRelation(Long userId, Long friendId);
    
    /**
     * 删除好友关系（双向删除）
     *
     * @param userId   用户ID
     * @param friendId 好友ID
     * @return 是否成功
     */
    boolean deleteFriend(Long userId, Long friendId);
    
    /**
     * 修改好友备注
     *
     * @param userId   用户ID
     * @param friendId 好友ID
     * @param remark   备注
     * @return 是否成功
     */
    boolean updateFriendRemark(Long userId, Long friendId, String remark);
    
    /**
     * 根据昵称搜索用户
     *
     * @param keyword 关键词
     * @param userId 当前用户ID
     * @return 用户列表
     */
    List<Map<String, Object>> searchUsersByNickname(String keyword, Long userId);
    
    /**
     * 搜索用户
     * @param keyword 关键词
     * @param userId 当前用户ID
     * @return 搜索结果
     */
    default List<Map<String, Object>> searchUsers(String keyword, Long userId) {
        // 默认调用通过昵称搜索的方法，保持向后兼容
        return searchUsersByNickname(keyword, userId);
    }
} 