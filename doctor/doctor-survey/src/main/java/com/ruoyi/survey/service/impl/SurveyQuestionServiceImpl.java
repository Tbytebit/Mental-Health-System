package com.doctor.survey.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.doctor.common.utils.DateUtils;
import com.doctor.survey.domain.SurveyQuestionOption;
import com.doctor.survey.domain.SurveyQuestionnaireQuestion;
import com.doctor.survey.mapper.*;
import com.doctor.survey.util.comparator.QuestionOrderComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doctor.survey.domain.SurveyQuestion;
import com.doctor.survey.service.ISurveyQuestionService;

/**
 * 调查问卷问题，用于存储问卷中的问题Service业务层处理
 * 
 * @author guo
 * @date 2024-01-19
 */
@Service
public class SurveyQuestionServiceImpl implements ISurveyQuestionService 
{
    @Autowired
    private SurveyQuestionMapper surveyQuestionMapper;

    @Autowired
    private SurveyQuestionnaireQuestionMapper surveyQuestionnaireQuestionMapper;

    @Autowired
    private SurveyQuestionOptionMapper surveyQuestionOptionMapper;

    @Autowired
    private SurveyOptionMapper surveyOptionMapper;

    @Autowired
    private SurveyQuestionnaireMapper surveyQuestionnaireMapper;

    /**
     * 查询调查问卷问题，用于存储问卷中的问题
     * 
     * @param questionId 调查问卷问题，用于存储问卷中的问题主键
     * @return 调查问卷问题，用于存储问卷中的问题
     */
    @Override
    public SurveyQuestion selectSurveyQuestionByQuestionId(String questionId)
    {
        return surveyQuestionMapper.selectSurveyQuestionByQuestionId(questionId);
    }

    /**
     * 查询调查问卷问题，用于存储问卷中的问题列表
     * 
     * @param surveyQuestion 调查问卷问题，用于存储问卷中的问题
     * @return 调查问卷问题，用于存储问卷中的问题
     */
    @Override
    public List<SurveyQuestion> selectSurveyQuestionList(SurveyQuestion surveyQuestion)
    {
        return surveyQuestionMapper.selectSurveyQuestionList(surveyQuestion);
    }

    /**
     * 新增调查问卷问题，用于存储问卷中的问题
     *
     * @param surveyQuestion 调查问卷问题，用于存储问卷中的问题
     * @return 结果
     */
    @Override
    public int insertSurveyQuestion(SurveyQuestion surveyQuestion)
    {
        surveyQuestion.setCreateTime(DateUtils.getNowDate());
        surveyQuestionMapper.insertSurveyQuestion(surveyQuestion);
        SurveyQuestionnaireQuestion surveyQuestionnaireQuestion = new SurveyQuestionnaireQuestion();
        surveyQuestionnaireQuestion.setQuestionnaire_id(surveyQuestion.getQuestionnaireId());
        surveyQuestionnaireQuestion.setQuestion_id(new Long(surveyQuestion.getQuestionId()));
        if(surveyQuestionnaireQuestionMapper.insert(surveyQuestionnaireQuestion)>0) {
            surveyQuestionnaireMapper.updateQuestionCount(String.valueOf(surveyQuestion.getQuestionnaireId()));
            return Integer.parseInt(surveyQuestion.getQuestionId());
        }
        else
            return 0;
    }

    /**
     * 修改调查问卷问题，用于存储问卷中的问题
     * 
     * @param surveyQuestion 调查问卷问题，用于存储问卷中的问题
     * @return 结果
     */
    @Override
    public int updateSurveyQuestion(SurveyQuestion surveyQuestion)
    {
        surveyQuestion.setUpdateTime(DateUtils.getNowDate());
        surveyQuestionMapper.updateSurveyQuestion(surveyQuestion);
        return Integer.parseInt(surveyQuestion.getQuestionId());
    }

    /**
     * 批量删除调查问卷问题，用于存储问卷中的问题
     * 
     * @param questionIds 需要删除的调查问卷问题，用于存储问卷中的问题主键
     * @return 结果
     */
    @Override
    public int deleteSurveyQuestionByQuestionIds(String[] questionIds)
    {
        for (String questionId : questionIds) {
            String questionnaireId = surveyQuestionnaireQuestionMapper.getQuestionnaireId(questionId);
            surveyQuestionnaireMapper.deQuestionnaireCount(questionnaireId);
            surveyQuestionnaireQuestionMapper.deleteByPrimaryKey(Long.valueOf(questionId));
            List<SurveyQuestionOption> surveyQuestionOptions =  surveyQuestionOptionMapper.selectListById(new Long(questionId));
            for(SurveyQuestionOption surveyQuestionOption : surveyQuestionOptions){
                surveyOptionMapper.deleteSurveyOptionByOptionId(String.valueOf(surveyQuestionOption.getOption_id()));
                surveyQuestionOptionMapper.deleteByPrimaryKey(surveyQuestionOption.getQuestion_id());
            }
        }
        return surveyQuestionMapper.deleteSurveyQuestionByQuestionIds(questionIds);
    }

    /**
     * 删除调查问卷问题，用于存储问卷中的问题信息
     * 
     * @param questionId 调查问卷问题，用于存储问卷中的问题主键
     * @return 结果
     */
    @Override
    public int deleteSurveyQuestionByQuestionId(String questionId)
    {
        String questionnaireId = surveyQuestionnaireQuestionMapper.getQuestionnaireId(questionId);
        surveyQuestionnaireMapper.deQuestionnaireCount(questionnaireId);
        surveyQuestionnaireQuestionMapper.deleteByPrimaryKey(new Long(questionId));
        return surveyQuestionMapper.deleteSurveyQuestionByQuestionId(questionId);
    }

    @Override
    public List<SurveyQuestion> selectSurveyQuestionListByQuestionId(String questionnaireId) {
        List<SurveyQuestionnaireQuestion> list =   surveyQuestionnaireQuestionMapper.selectListByQuestionnaireId(new Long(questionnaireId));
        List<SurveyQuestion> surveyQuestions = new ArrayList<>(list.size());
        for (SurveyQuestionnaireQuestion surveyQuestionnaireQuestion : list) {
            SurveyQuestion up = surveyQuestionMapper.selectSurveyQuestionByQuestionId(String.valueOf(surveyQuestionnaireQuestion.getQuestion_id()));
            surveyQuestions.add(up);
        }
        surveyQuestions.sort(new QuestionOrderComparator());

        return surveyQuestions;
    }
}
