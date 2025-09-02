package com.doctor.survey.service;

import java.util.List;
import com.doctor.survey.domain.SurveyOption;

/**
 * 调查问卷问题选项存储Service接口
 * 
 * @author guo
 * @date 2024-01-19
 */
public interface ISurveyOptionService 
{
    /**
     * 查询调查问卷问题选项存储
     * 
     * @param optionId 调查问卷问题选项存储主键
     * @return 调查问卷问题选项存储
     */
    public SurveyOption selectSurveyOptionByOptionId(String optionId);

    /**
     * 查询调查问卷问题选项存储列表
     * 
     * @param surveyOption 调查问卷问题选项存储
     * @return 调查问卷问题选项存储集合
     */
    public List<SurveyOption> selectSurveyOptionList(SurveyOption surveyOption);

    /**
     * 新增调查问卷问题选项存储
     * 
     * @param surveyOption 调查问卷问题选项存储
     * @return 结果
     */
    public int insertSurveyOption(SurveyOption surveyOption);

    /**
     * 修改调查问卷问题选项存储
     * 
     * @param surveyOption 调查问卷问题选项存储
     * @return 结果
     */
    public int updateSurveyOption(SurveyOption surveyOption);

    /**
     * 批量删除调查问卷问题选项存储
     * 
     * @param optionIds 需要删除的调查问卷问题选项存储主键集合
     * @return 结果
     */
    public int deleteSurveyOptionByOptionIds(String[] optionIds);

    /**
     * 删除调查问卷问题选项存储信息
     * 
     * @param optionId 调查问卷问题选项存储主键
     * @return 结果
     */
    public int deleteSurveyOptionByOptionId(String optionId);

    List<SurveyOption> selectSurveyOptionListByQuestionId(String questionId);
}
