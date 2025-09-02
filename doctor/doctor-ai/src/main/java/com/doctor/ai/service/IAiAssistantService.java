package com.doctor.ai.service;

import com.doctor.ai.domain.AiAssistant;

import java.util.List;

/**
 * AI助手服务接口
 */
public interface IAiAssistantService 
{
    /**
     * 查询AI助手
     * 
     * @param assistantId 助手ID
     * @return AI助手
     */
    public AiAssistant selectAiAssistantById(Long assistantId);

    /**
     * 查询AI助手列表
     * 
     * @param aiAssistant 助手查询条件
     * @return AI助手集合
     */
    public List<AiAssistant> selectAiAssistantList(AiAssistant aiAssistant);

    /**
     * 获取有效的AI助手列表
     * 
     * @return AI助手集合
     */
    public List<AiAssistant> selectActiveAiAssistants();

    /**
     * 获取系统提示词
     * 
     * @param assistantType 助手类型
     * @return 系统提示词
     */
    public String getSystemPrompt(Integer assistantType);
} 