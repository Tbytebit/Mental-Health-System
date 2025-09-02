package com.doctor.ai.service.impl;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;
import com.doctor.ai.domain.AiAssistant;
import com.doctor.ai.mapper.AiAssistantMapper;
import com.doctor.ai.service.IAiAssistantService;

import javax.annotation.Resource;

/**
 * AI助手服务实现
 */
@Service
public class AiAssistantServiceImpl implements IAiAssistantService
{
    @Resource
    private AiAssistantMapper aiAssistantMapper;

    /**
     * 查询AI助手
     * 
     * @param assistantId 助手ID
     * @return AI助手
     */
    @Override
    public AiAssistant selectAiAssistantById(Long assistantId)
    {
        return aiAssistantMapper.selectAiAssistantById(assistantId);
    }

    /**
     * 查询AI助手列表
     * 
     * @param aiAssistant AI助手
     * @return AI助手
     */
    @Override
    public List<AiAssistant> selectAiAssistantList(AiAssistant aiAssistant)
    {
        return aiAssistantMapper.selectAiAssistantList(aiAssistant);
    }

    /**
     * 获取有效的AI助手列表
     * 
     * @return AI助手集合
     */
    @Override
    public List<AiAssistant> selectActiveAiAssistants()
    {
        AiAssistant aiAssistant = new AiAssistant();
        aiAssistant.setStatus("0"); // 0代表正常状态
        return aiAssistantMapper.selectAiAssistantList(aiAssistant);
    }

    /**
     * 获取系统提示词
     * 
     * @param assistantType 助手类型
     * @return 系统提示词
     */
    @Override
    public String getSystemPrompt(Integer assistantType)
    {
        // 如果数据库中没有配置，则使用默认提示词
        String defaultPrompt = "";
        switch (assistantType) {
            case 0: // 心理顾问
                defaultPrompt = "你是一名专业的心理健康顾问，擅长提供情绪管理和心理健康建议。你的回答应该温暖、专业，并基于心理学知识。避免给出医疗诊断，而是提供支持和建议。";
                break;
            case 1: // 冥想助手
                defaultPrompt = "你是一名冥想指导师，擅长引导用户进行冥想和放松练习。你的回答应该平静、舒缓，帮助用户减轻压力和焦虑。提供简单易行的冥想技巧和呼吸练习。";
                break;
            case 2: // 情绪分析师
                defaultPrompt = "你是一名情绪分析专家，擅长帮助用户识别、理解和管理自己的情绪。你的回答应该客观、分析性强，帮助用户深入了解自己的情感反应和模式。";
                break;
            case 3: // 生活教练
                defaultPrompt = "你是一名生活教练，擅长帮助用户设定目标、培养健康习惯和提高生活质量。你的回答应该积极、实用，提供具体的行动建议和激励。";
                break;
            default:
                defaultPrompt = "你是一名AI助手，致力于提供有用、准确、安全的回答。";
        }

        // 尝试从数据库获取配置的提示词
        AiAssistant query = new AiAssistant();
        query.setAssistantType(assistantType);
        query.setStatus("0"); // 正常状态
        List<AiAssistant> assistants = aiAssistantMapper.selectAiAssistantList(query);
        
        if (assistants != null && !assistants.isEmpty() && assistants.get(0).getSystemPrompt() != null) {
            return assistants.get(0).getSystemPrompt();
        }
        
        return defaultPrompt;
    }
} 