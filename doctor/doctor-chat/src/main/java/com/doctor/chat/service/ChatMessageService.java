package com.doctor.chat.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.doctor.chat.dto.ChatMessageDTO;
import com.doctor.chat.entity.ChatMessage;

import java.util.List;

/**
 * 聊天消息Service接口
 */
public interface ChatMessageService extends IService<ChatMessage> {
    
    /**
     * 发送消息
     *
     * @param message
     * @return 是否成功
     */
    boolean sendMessage(ChatMessage message);
    
    /**
     * 保存消息
     *
     * @param chatMessage 聊天消息
     * @return 是否成功
     */
    boolean save(ChatMessage chatMessage);
    
    /**
     * 获取两个用户之间的聊天记录
     *
     * @param userId1 用户ID1
     * @param userId2 用户ID2
     * @param limit   限制条数
     * @return 聊天记录列表
     */
    //List<ChatMessageDTO> getChatHistory(Long userId1, Long userId2, Integer limit);
    List<ChatMessageDTO> getChatHistory(Long userId1, Long userId2, Long requesterId, Integer limit);
    /**
     * 获取未读消息数量
     *
     * @param receiverId 接收者ID
     * @return 未读消息数量
     */
    int getUnreadCount(Long receiverId);
    
    /**
     * 标记消息为已读
     *
     * @param senderId   发送者ID
     * @param receiverId 接收者ID
     * @return 是否成功
     */
    boolean markMessageRead(Long senderId, Long receiverId);
    
    /**
     * 标记会话中的消息为已读
     *
     * @param conversationId 会话ID
     * @param receiverId 接收者ID
     * @return 是否成功
     */
    boolean markMessageReadByConversation(String conversationId, Long receiverId);
    
    /**
     * 获取指定用户的未读消息
     *
     * @param userId 用户ID
     * @return 未读消息列表
     */
    List<ChatMessage> getUnreadMessages(Long userId);
    
    /**
     * 统计指定用户的未读消息数量
     *
     * @param userId 用户ID
     * @return 未读消息数
     */
    int countUnreadMessages(Long userId);
    
    /**
     * 统计指定会话的未读消息数量
     *
     * @param userId 用户ID
     * @param conversationId 会话ID
     * @return 未读消息数
     */
    int countUnreadMessagesByConversation(Long userId, String conversationId);
    
    /**
     * 获取聊天记录实体列表
     *
     * @param userId1 用户ID1
     * @param userId2 用户ID2
     * @param limit   限制条数
     * @return 聊天记录实体列表
     */
    List<ChatMessage> getChatHistoryEntities(Long userId1, Long userId2, Integer limit);
    
    /**
     * 获取指定会话的聊天记录
     *
     * @param conversationId 会话ID
     * @param limit   限制条数
     * @return 聊天记录
     */
    List<ChatMessageDTO> getMessagesByConversationId(String conversationId, Integer limit);
    
    /**
     * 清空聊天记录
     * 不再删除数据库中的记录，而是记录清空时间，后续查询时过滤
     *
     * @param userId 用户ID
     * @param friendId 好友ID
     * @return 是否成功
     */
    boolean clearChatMessages(Long userId, Long friendId);
} 