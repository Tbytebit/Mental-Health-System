package com.doctor.survey.mapper;

import java.util.List;
import com.doctor.survey.domain.SurveyQuestion;

/**
 * 调查问卷问题，用于存储问卷中的问题Mapper接口
 * 
 * @author guo
 * @date 2024-01-19
 */
public interface SurveyQuestionMapper 
{
    /**
     * 查询调查问卷问题，用于存储问卷中的问题
     * 
     * @param questionId 调查问卷问题，用于存储问卷中的问题主键
     * @return 调查问卷问题，用于存储问卷中的问题
     */
    public SurveyQuestion selectSurveyQuestionByQuestionId(String questionId);

    /**
     * 查询调查问卷问题，用于存储问卷中的问题列表
     * 
     * @param surveyQuestion 调查问卷问题，用于存储问卷中的问题
     * @return 调查问卷问题，用于存储问卷中的问题集合
     */
    public List<SurveyQuestion> selectSurveyQuestionList(SurveyQuestion surveyQuestion);

    /**
     * 新增调查问卷问题，用于存储问卷中的问题
     * 
     * @param surveyQuestion 调查问卷问题，用于存储问卷中的问题
     * @return 结果
     */
    public int insertSurveyQuestion(SurveyQuestion surveyQuestion);

    /**
     * 修改调查问卷问题，用于存储问卷中的问题
     * 
     * @param surveyQuestion 调查问卷问题，用于存储问卷中的问题
     * @return 结果
     */
    public int updateSurveyQuestion(SurveyQuestion surveyQuestion);

    /**
     * 删除调查问卷问题，用于存储问卷中的问题
     * 
     * @param questionId 调查问卷问题，用于存储问卷中的问题主键
     * @return 结果
     */
    public int deleteSurveyQuestionByQuestionId(String questionId);

    /**
     * 批量删除调查问卷问题，用于存储问卷中的问题
     * 
     * @param questionIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSurveyQuestionByQuestionIds(String[] questionIds);
}
