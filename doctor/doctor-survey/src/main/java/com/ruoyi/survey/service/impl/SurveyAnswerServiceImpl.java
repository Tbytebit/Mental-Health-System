package com.doctor.survey.service.impl;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.doctor.survey.domain.SurveyOptionAnswer;
import com.doctor.survey.domain.SurveyQuestionAnswer;
import com.doctor.survey.mapper.SurveyOptionAnswerMapper;
import com.doctor.survey.mapper.SurveyQuestionAnswerMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doctor.survey.mapper.SurveyAnswerMapper;
import com.doctor.survey.domain.SurveyAnswer;
import com.doctor.survey.service.ISurveyAnswerService;

/**
 * 答卷的问题答案存储Service业务层处理
 * 
 * @author guo
 * @date 2024-01-19
 */
@Service
public class SurveyAnswerServiceImpl implements ISurveyAnswerService 
{
    @Autowired
    private SurveyAnswerMapper surveyAnswerMapper;

    @Autowired
    private SurveyOptionAnswerMapper surveyOptionAnswerMapper;

    @Autowired
    private SurveyQuestionAnswerMapper surveyQuestionAnswerMapper;

    /**
     * 查询答卷的问题答案存储
     * 
     * @param answerId 答卷的问题答案存储主键
     * @return 答卷的问题答案存储
     */
    @Override
    public SurveyAnswer selectSurveyAnswerByAnswerId(String answerId)
    {

        return surveyAnswerMapper.selectSurveyAnswerByAnswerId(answerId);
    }

    /**
     * 查询答卷的问题答案存储列表
     * 
     * @param surveyAnswer 答卷的问题答案存储
     * @return 答卷的问题答案存储
     */
    @Override
    public List<SurveyAnswer> selectSurveyAnswerList(SurveyAnswer surveyAnswer)
    {
        return surveyAnswerMapper.selectSurveyAnswerList(surveyAnswer);
    }

    /**
     * 新增答卷的问题答案存储
     * 
     * @param surveyAnswer 答卷的问题答案存储
     * @return 结果
     */
    @Override
    public int insertSurveyAnswer(SurveyAnswer surveyAnswer)
    {
        surveyAnswerMapper.insertSurveyAnswer(surveyAnswer);
        if(Objects.equals(surveyAnswer.getAnswerType(), "radio") || Objects.equals(surveyAnswer.getAnswerType(), "checkbox")) {
            String[] optionIds = surveyAnswer.getAnswer().split(";");
            for (String optionId : optionIds) {
                SurveyOptionAnswer surveyOptionAnswer = new SurveyOptionAnswer();
                surveyOptionAnswer.setAnswer_id(Long.valueOf(surveyAnswer.getAnswerId()));
                surveyOptionAnswer.setOption_id(Long.valueOf(optionId));
                surveyOptionAnswerMapper.insert(surveyOptionAnswer);
            }
        }
        SurveyQuestionAnswer surveyQuestionAnswer = new SurveyQuestionAnswer();
        surveyQuestionAnswer.setAnswer_id(Long.valueOf(surveyAnswer.getAnswerId()));
        surveyQuestionAnswer.setQuestion_id(Long.valueOf(surveyAnswer.getQuestionId()));
        return surveyQuestionAnswerMapper.insert(surveyQuestionAnswer);
    }

    /**
     * 修改答卷的问题答案存储
     * 
     * @param surveyAnswer 答卷的问题答案存储
     * @return 结果
     */
    @Override
    public int updateSurveyAnswer(SurveyAnswer surveyAnswer)
    {
        return surveyAnswerMapper.updateSurveyAnswer(surveyAnswer);
    }

    /**
     * 批量删除答卷的问题答案存储
     * 
     * @param answerIds 需要删除的答卷的问题答案存储主键
     * @return 结果
     */
    @Override
    public int deleteSurveyAnswerByAnswerIds(String[] answerIds)
    {
        surveyOptionAnswerMapper.deleteByAnswerIds(answerIds);
        surveyQuestionAnswerMapper.deleteByAnswerIds(answerIds);
        return surveyAnswerMapper.deleteSurveyAnswerByAnswerIds(answerIds);
    }

    /**
     * 删除答卷的问题答案存储信息
     * 
     * @param answerId 答卷的问题答案存储主键
     * @return 结果
     */
    @Override
    public int deleteSurveyAnswerByAnswerId(String answerId)
    {
        return surveyAnswerMapper.deleteSurveyAnswerByAnswerId(answerId);
    }
}
