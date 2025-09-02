package com.doctor.ai.mapper;

import java.util.List;
import java.util.Map;
import com.doctor.ai.domain.AiMessage;

/**
 * AI消息Mapper接口
 */
public interface AiMessageMapper 
{
    /**
     * 查询AI消息
     * 
     * @param messageId 消息ID
     * @return AI消息
     */
    public AiMessage selectAiMessageById(Long messageId);

    /**
     * 查询指定聊天的消息列表
     * 
     * @param params 查询参数，包含chatId, beforeId, limit
     * @return AI消息集合
     */
    public List<AiMessage> selectMessagesByChatId(Map<String, Object> params);

    /**
     * 新增AI消息
     * 
     * @param aiMessage AI消息
     * @return 结果
     */
    public int insertAiMessage(AiMessage aiMessage);

    /**
     * 修改AI消息
     * 
     * @param aiMessage AI消息
     * @return 结果
     */
    public int updateAiMessage(AiMessage aiMessage);

    /**
     * 删除AI消息
     * 
     * @param messageId 消息ID
     * @return 结果
     */
    public int deleteAiMessageById(Long messageId);

    /**
     * 删除指定聊天的所有消息
     * 
     * @param chatId 聊天ID
     * @return 结果
     */
    public int deleteMessagesByChatId(Long chatId);

    /**
     * 批量删除AI消息
     * 
     * @param messageIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteAiMessageByIds(Long[] messageIds);
} 