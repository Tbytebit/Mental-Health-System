package com.doctor.survey.service;

import java.util.List;
import com.doctor.survey.domain.SurveyAnswer;

/**
 * 答卷的问题答案存储Service接口
 * 
 * @author guo
 * @date 2024-01-19
 */
public interface ISurveyAnswerService 
{
    /**
     * 查询答卷的问题答案存储
     * 
     * @param answerId 答卷的问题答案存储主键
     * @return 答卷的问题答案存储
     */
    public SurveyAnswer selectSurveyAnswerByAnswerId(String answerId);

    /**
     * 查询答卷的问题答案存储列表
     * 
     * @param surveyAnswer 答卷的问题答案存储
     * @return 答卷的问题答案存储集合
     */
    public List<SurveyAnswer> selectSurveyAnswerList(SurveyAnswer surveyAnswer);

    /**
     * 新增答卷的问题答案存储
     * 
     * @param surveyAnswer 答卷的问题答案存储
     * @return 结果
     */
    public int insertSurveyAnswer(SurveyAnswer surveyAnswer);

    /**
     * 修改答卷的问题答案存储
     * 
     * @param surveyAnswer 答卷的问题答案存储
     * @return 结果
     */
    public int updateSurveyAnswer(SurveyAnswer surveyAnswer);

    /**
     * 批量删除答卷的问题答案存储
     * 
     * @param answerIds 需要删除的答卷的问题答案存储主键集合
     * @return 结果
     */
    public int deleteSurveyAnswerByAnswerIds(String[] answerIds);

    /**
     * 删除答卷的问题答案存储信息
     * 
     * @param answerId 答卷的问题答案存储主键
     * @return 结果
     */
    public int deleteSurveyAnswerByAnswerId(String answerId);
}
