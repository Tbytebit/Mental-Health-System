package com.doctor.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doctor.chat.entity.ChatMessage;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.annotations.Delete;

import java.util.Date;
import java.util.List;

/**
 * 聊天消息Mapper接口
 */
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
    
    /**
     * 获取两个用户之间的聊天记录
     *
     * @param userId1 用户ID1
     * @param userId2 用户ID2
     * @param limit   限制条数
     * @param clearTime 清空时间，如果不为null则只返回该时间之后的消息
     * @return 聊天记录列表
     */
    List<ChatMessage> getChatHistory(@Param("userId1") Long userId1, @Param("userId2") Long userId2, @Param("limit") Integer limit, @Param("clearTime") Date clearTime);
    
    /**
     * 获取未读消息数量
     *
     * @param receiverId 接收者ID
     * @return 未读消息数量
     */
    int getUnreadCount(@Param("receiverId") Long receiverId);
    
    /**
     * 标记消息为已读
     *
     * @param senderId   发送者ID
     * @param receiverId 接收者ID
     * @return 影响行数
     */
    @Update("UPDATE chat_message SET status = 1, update_time = NOW() WHERE sender_id = #{senderId} AND receiver_id = #{receiverId} AND status = 0")
    int markMessageRead(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);
    
    /**
     * 根据会话ID获取聊天记录
     *
     * @param conversationId 会话ID
     * @param limit 限制条数
     * @param clearTime 清空时间，如果不为null则只返回该时间之后的消息
     * @return 聊天记录列表
     */
    List<ChatMessage> getMessagesByConversationId(@Param("conversationId") String conversationId, @Param("limit") Integer limit, @Param("clearTime") Date clearTime);
    
    /**
     * 获取指定会话未读消息数量
     *
     * @param receiverId 接收者ID
     * @param conversationId 会话ID
     * @return 未读消息数量
     */
    int countUnreadMessagesByConversation(@Param("receiverId") Long receiverId, @Param("conversationId") String conversationId);
    
    /**
     * 清空两个用户之间的聊天记录
     *
     * @param userId1 用户ID1
     * @param userId2 用户ID2
     * @return 影响行数
     */
    @Delete("DELETE FROM chat_message WHERE (sender_id = #{userId1} AND receiver_id = #{userId2}) OR (sender_id = #{userId2} AND receiver_id = #{userId1})")
    int clearChatMessages(@Param("userId1") Long userId1, @Param("userId2") Long userId2);
} 