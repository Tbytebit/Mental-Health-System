package com.doctor.ai.mapper;

import java.util.List;
import com.doctor.ai.domain.AiAssistant;

/**
 * AI助手Mapper接口
 */
public interface AiAssistantMapper 
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
     * 新增AI助手
     * 
     * @param aiAssistant AI助手
     * @return 结果
     */
    public int insertAiAssistant(AiAssistant aiAssistant);

    /**
     * 修改AI助手
     * 
     * @param aiAssistant AI助手
     * @return 结果
     */
    public int updateAiAssistant(AiAssistant aiAssistant);

    /**
     * 删除AI助手
     * 
     * @param assistantId 助手ID
     * @return 结果
     */
    public int deleteAiAssistantById(Long assistantId);

    /**
     * 批量删除AI助手
     * 
     * @param assistantIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteAiAssistantByIds(Long[] assistantIds);
} 