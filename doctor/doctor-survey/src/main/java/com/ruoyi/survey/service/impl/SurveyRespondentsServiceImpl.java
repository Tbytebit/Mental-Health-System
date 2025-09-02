package com.doctor.survey.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;


import com.doctor.common.core.domain.entity.SysUser;
import com.doctor.common.core.domain.model.LoginUser;
import com.doctor.common.utils.SecurityUtils;
import com.doctor.survey.domain.*;
import com.doctor.survey.mapper.*;
import com.doctor.survey.util.comparator.QuestionOrderComparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doctor.survey.service.ISurveyRespondentsService;

/**
 * 问卷答卷存储Service业务层处理
 * 
 * @author guo
 * @date 2024-01-19
 */
@Service
public class SurveyRespondentsServiceImpl implements ISurveyRespondentsService 
{
    @Autowired
    private SurveyRespondentsMapper surveyRespondentsMapper;

    @Autowired
    private SurveyQuestionnaireRespondentsMapper surveyQuestionnaireRespondentsMapper;

    @Autowired
    private SurveyRespondentsAnswerMapper surveyRespondentsAnswerMapper;

    @Autowired
    private SurveyOptionAnswerMapper surveyOptionAnswerMapper;

    @Autowired
    private SurveyQuestionnaireQuestionMapper surveyQuestionnaireQuestionMapper;

    @Autowired
    private SurveyQuestionMapper surveyQuestionMapper;

    @Autowired
    private SurveyQuestionOptionMapper surveyQuestionOptionMapper;

    @Autowired
    private SurveyOptionMapper surveyOptionMapper;

    @Autowired
    private SurveyAnswerMapper surveyAnswerMapper;

    @Autowired
    private SurveyQuestionnaireMapper surveyQuestionnaireMapper;

    @Autowired
    private SurveyUserRespondentsMapper surveyUserRespondentsMapper;

    /**
     * 查询问卷答卷存储
     * 
     * @param respondentsId 问卷答卷存储主键
     * @return 问卷答卷存储
     */
    @Override
    public SurveyRespondents selectSurveyRespondentsByRespondentsId(String respondentsId)
    {
        return surveyRespondentsMapper.selectSurveyRespondentsByRespondentsId(respondentsId);
    }

    /**
     * 查询问卷答卷存储列表
     * 
     * @param surveyRespondents 问卷答卷存储
     * @return 问卷答卷存储
     */
    @Override
    public List<SurveyRespondents> selectSurveyRespondentsList(SurveyRespondents surveyRespondents)
    {
        return surveyRespondentsMapper.selectSurveyRespondentsList(surveyRespondents);
    }

    /**
     * 新增问卷答卷存储
     * 
     * @param surveyRespondents 问卷答卷存储
     * @return 结果
     */
    @Override
    public int insertSurveyRespondents(SurveyRespondents surveyRespondents)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        surveyRespondents.setUserName(user.getUserName());
        Date currentDate = new Date();
        surveyRespondents.setSubmitTime(currentDate);
        surveyRespondentsMapper.insertSurveyRespondents(surveyRespondents);
        SurveyQuestionnaireRespondents surveyQuestionnaireRespondents = new SurveyQuestionnaireRespondents();
        surveyQuestionnaireRespondents.setRespondents_id(Long.valueOf(surveyRespondents.getRespondentsId()));
        surveyQuestionnaireRespondents.setQuestionnaire_id(Long.valueOf(surveyRespondents.getQuestionnaireId()));
        surveyQuestionnaireMapper.updateSurveyRespondentsCount(surveyRespondents.getQuestionnaireId());
        return surveyQuestionnaireRespondentsMapper.insert(surveyQuestionnaireRespondents);
    }

    /**
     * 修改问卷答卷存储
     * 
     * @param surveyRespondents 问卷答卷存储
     * @return 结果
     */
    @Override
    public int updateSurveyRespondents(SurveyRespondents surveyRespondents)
    {
        return surveyRespondentsMapper.updateSurveyRespondents(surveyRespondents);
    }

    /**
     * 批量删除问卷答卷存储
     * 
     * @param respondentsIds 需要删除的问卷答卷存储主键
     * @return 结果
     */
    @Override
    public int deleteSurveyRespondentsByRespondentsIds(String[] respondentsIds)
    {
       String[] answersIds = surveyRespondentsAnswerMapper.selectByRespondentsIds(respondentsIds);
       surveyOptionAnswerMapper.deleteByAnswerIds(answersIds);
       surveyRespondentsAnswerMapper.deleteByRespondentsIds(respondentsIds);
       surveyQuestionnaireRespondentsMapper.deleteByRespondentsIds(respondentsIds);
        return surveyRespondentsMapper.deleteSurveyRespondentsByRespondentsIds(respondentsIds);
    }

    /**
     * 删除问卷答卷存储信息
     * 
     * @param respondentsId 问卷答卷存储主键
     * @return 结果
     */
    @Override
    public int deleteSurveyRespondentsByRespondentsId(String respondentsId)
    {
        SurveyQuestionnaireRespondents surveyQuestionnaireRespondents =  surveyQuestionnaireRespondentsMapper.selectByRespondentsId(respondentsId);
        surveyQuestionnaireMapper.deSurveyRespondentsCount(surveyQuestionnaireRespondents.getQuestionnaire_id());
        return surveyRespondentsMapper.deleteSurveyRespondentsByRespondentsId(respondentsId);
    }

    @Override
    public List<SurveyRespondents> selectSurveyRespondentsListByQuestionnaireId(String questionnaireId) {
        String[] respondentsIds = surveyQuestionnaireRespondentsMapper.selectByQuestionnaireId(questionnaireId);
        if(respondentsIds.length != 0) {
            return surveyRespondentsMapper.selectSurveyRespondentsByRespondentsIds(respondentsIds);
        } else {
            return new ArrayList<SurveyRespondents>();
        }
    }

    @Override
    public SurveyInfo selectSurveyInfoByRespondentsId(String respondentsId) {
        SurveyRespondents surveyRespondents = surveyRespondentsMapper.selectSurveyRespondentsByRespondentsId(respondentsId);
        SurveyInfo surveyInfo = new SurveyInfo();
        surveyInfo.setUserName(surveyRespondents.getUserName());
        surveyInfo.setRespondentsId(surveyRespondents.getRespondentsId());
        surveyInfo.setQuestionnaireId(surveyRespondents.getQuestionnaireId());
        surveyInfo.setSubmitTime(surveyRespondents.getSubmitTime());
        String questionnaireId = String.valueOf(surveyQuestionnaireRespondentsMapper.selectByRespondentsId(respondentsId).getQuestionnaire_id());
        List<SurveyQuestionnaireQuestion> surveyQuestionnaireQuestions = surveyQuestionnaireQuestionMapper.selectListByQuestionnaireId(Long.valueOf(questionnaireId));
        List<String> questionIds = new ArrayList<>();
        for(SurveyQuestionnaireQuestion surveyQuestionnaireQuestion : surveyQuestionnaireQuestions){
            questionIds.add(String.valueOf(surveyQuestionnaireQuestion.getQuestion_id()));
        }
        List<SurveyInfoQuestion> surveyInfoQuestions = new ArrayList<>();
        for(String questionId :questionIds){
           SurveyQuestion surveyQuestion = surveyQuestionMapper.selectSurveyQuestionByQuestionId(questionId);
           SurveyInfoQuestion surveyInfoQuestion = new SurveyInfoQuestion();
           surveyInfoQuestion.setQuestionId(surveyQuestion.getQuestionId());
           surveyInfoQuestion.setQuestionOrder(surveyQuestion.getQuestionOrder());
           surveyInfoQuestion.setQuestionTitle(surveyQuestion.getQuestionType());
           surveyInfoQuestion.setQuestionnaireId(surveyQuestion.getQuestionnaireId());
           surveyInfoQuestion.setQuestionType(surveyQuestion.getQuestionType());

           if(Objects.equals(surveyQuestion.getQuestionType(), "radio") || Objects.equals(surveyQuestion.getQuestionType(), "checkbox")) {
               List<SurveyQuestionOption> surveyQuestionOptions = surveyQuestionOptionMapper.selectListById(Long.valueOf(questionId));
               List<SurveyOption> surveyOptions = new ArrayList<>();
               for (SurveyQuestionOption surveyQuestionOption : surveyQuestionOptions) {
                   SurveyOption surveyOption = surveyOptionMapper.selectSurveyOptionByOptionId(String.valueOf(surveyQuestionOption.getOption_id()));
                   surveyOptions.add(surveyOption);
               }
               surveyInfoQuestion.setOptionList(surveyOptions);
           }
           SurveyAnswer surveyAnswer = new SurveyAnswer();
           surveyAnswer.setRespondentsId(respondentsId);
           surveyAnswer.setQuestionId(questionId);
           SurveyAnswer surveyAnswerTmp =  surveyAnswerMapper.selectSurveyAnswerByAnswer(surveyAnswer);
           if(surveyAnswerTmp != null) {
               surveyInfoQuestion.setAnswers(surveyAnswerTmp.getAnswer());
           }
           surveyInfoQuestions.add(surveyInfoQuestion);
           surveyInfoQuestions.sort(new QuestionOrderComparator());
        }
        surveyInfo.setSurveyInfoQuestionList(surveyInfoQuestions);
        return surveyInfo;
    }
}
