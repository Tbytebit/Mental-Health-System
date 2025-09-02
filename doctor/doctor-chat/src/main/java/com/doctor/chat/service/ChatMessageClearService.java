package com.doctor.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.doctor.chat.entity.ChatMessageClear;

import java.util.Date;

/**
 * 聊天记录清空记录Service接口
 */
public interface ChatMessageClearService extends IService<ChatMessageClear> {
    
    /**
     * 记录用户清空聊天记录
     *
     * @param userId 用户ID
     * @param friendId 好友ID
     * @param conversationId 会话ID
     * @return 是否成功
     */
    boolean recordClearHistory(Long userId, Long friendId, String conversationId);
    
    /**
     * 获取用户最近一次清空聊天记录的时间
     *
     * @param userId 用户ID
     * @param friendId 好友ID
     * @return 清空时间，如果没有清空过返回null
     */
    Date getLastClearTime(Long userId, Long friendId);
} 