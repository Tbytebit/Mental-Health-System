package com.doctor.ai.service;

import com.doctor.ai.domain.AiChat;
import com.doctor.ai.domain.AiMessage;

import java.util.List;
import java.util.Map;

/**
 * AI聊天服务接口
 */
public interface IAiChatService 
{
    /**
     * 查询AI聊天
     * 
     * @param chatId 聊天ID
     * @return AI聊天
     */
    public AiChat selectAiChatById(Long chatId);

    /**
     * 查询用户的AI聊天列表
     * 
     * @param userId 用户ID
     * @return AI聊天集合
     */
    public List<AiChat> selectAiChatListByUserId(Long userId);

    /**
     * 创建AI聊天
     * 
     * @param aiChat 聊天信息
     * @return 结果
     */
    public AiChat insertAiChat(AiChat aiChat);

    /**
     * 删除AI聊天
     * 
     * @param chatId 聊天ID
     * @return 结果
     */
    public int deleteAiChatById(Long chatId);

    /**
     * 删除单个AI消息
     * 
     * @param messageId 消息ID
     * @param userId 用户ID，用于权限验证
     * @return 结果
     */
    public int deleteAiMessageById(Long messageId, Long userId);

    /**
     * 清空特定聊天的历史记录
     * 
     * @param chatId 聊天ID
     * @param userId 用户ID，用于权限验证
     * @return 结果
     */
    public int clearChatHistory(Long chatId, Long userId);

    /**
     * 清空用户所有聊天的历史记录
     * 
     * @param userId 用户ID
     * @return 结果
     */
    public int clearAllChatHistory(Long userId);

    /**
     * 获取聊天历史消息
     * 
     * @param chatId 聊天ID
     * @param beforeId 消息ID（获取此ID之前的消息）
     * @param limit 限制条数
     * @return 消息列表
     */
    public List<AiMessage> getChatHistory(Long chatId, Long beforeId, Integer limit);

    /**
     * 发送消息给AI并获取回复
     * 
     * @param userId 用户ID
     * @param chatId 聊天ID
     * @param assistantType 助手类型
     * @param content 消息内容
     * @return AI回复消息
     */
    public AiMessage sendMessageToAI(Long userId, Long chatId, Integer assistantType, String content);

    /**
     * 分析情绪量表结果
     * 
     * @param questions 问题列表
     * @param answers 回答列表
     * @param scaleId 量表ID
     * @param totalScore 总分
     * @return AI分析结果
     */
    public String analyzeMoodScaleResult(List<String> questions, List<String> answers, Long scaleId, Integer totalScore);
} 