package com.doctor.ai.mapper;

import java.util.List;
import com.doctor.ai.domain.AiChat;

/**
 * AI聊天Mapper接口
 */
public interface AiChatMapper 
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
     * 新增AI聊天
     * 
     * @param aiChat AI聊天
     * @return 结果
     */
    public int insertAiChat(AiChat aiChat);

    /**
     * 修改AI聊天
     * 
     * @param aiChat AI聊天
     * @return 结果
     */
    public int updateAiChat(AiChat aiChat);

    /**
     * 删除AI聊天
     * 
     * @param chatId 聊天ID
     * @return 结果
     */
    public int deleteAiChatById(Long chatId);

    /**
     * 批量删除AI聊天
     * 
     * @param chatIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteAiChatByIds(Long[] chatIds);
} 