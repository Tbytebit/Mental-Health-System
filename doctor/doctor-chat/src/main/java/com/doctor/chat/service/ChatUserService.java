package com.doctor.chat.service;

import java.util.List;
import java.util.Map;
import com.doctor.chat.entity.ChatUser;

/**
 * 聊天用户服务接口
 */
public interface ChatUserService {
    
    /**
     * 更新用户状态
     * @param userId 用户ID
     * @param status 状态（1=在线，0=离线）
     * @return 是否成功
     */
    boolean updateUserStatus(Long userId, String status);
    
    /**
     * 获取用户状态
     * @param userId 用户ID
     * @return 状态（1=在线，0=离线）
     */
    String getUserStatus(Long userId);
    
    /**
     * 批量获取用户状态
     * @param userIds 用户ID列表
     * @return 用户ID与状态的映射
     */
    Map<String, String> getBatchUserStatus(List<Long> userIds);
    
    /**
     * 清理超时离线用户
     * @param timeoutMillis 超时时间（毫秒）
     * @return 清理的用户数量
     */
    int cleanTimeoutUsers(long timeoutMillis);
    
    /**
     * 保存用户信息
     * @param user 用户信息
     * @return 是否成功
     */
    boolean saveUser(ChatUser user);
    
    /**
     * 从sys_user表获取用户信息
     * @param userId 用户ID
     * @return 用户信息
     */
    Map<String, Object> getUserInfoFromSysUser(Long userId);
} 