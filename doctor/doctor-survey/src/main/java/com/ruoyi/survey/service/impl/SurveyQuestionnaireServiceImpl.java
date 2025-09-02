package com.doctor.survey.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.doctor.common.core.domain.entity.SysUser;
import com.doctor.common.core.domain.model.LoginUser;
import com.doctor.common.utils.DateUtils;
import com.doctor.common.utils.SecurityUtils;
import com.doctor.survey.domain.*;
import com.doctor.survey.mapper.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doctor.survey.service.ISurveyQuestionnaireService;

/**
 * 问卷中心Service业务层处理
 * 
 * @author guo
 * @date 2024-01-19
 */
@Service
public class SurveyQuestionnaireServiceImpl implements ISurveyQuestionnaireService 
{
    @Autowired
    private SurveyQuestionnaireMapper surveyQuestionnaireMapper;

    @Autowired
    private SurveyQuestionnaireQuestionMapper surveyQuestionnaireQuestionMapper;

    @Autowired
    private SurveyQuestionOptionMapper surveyQuestionOptionMapper;

    @Autowired
    private SurveyQuestionMapper surveyQuestionMapper;

    @Autowired
    private SurveyOptionMapper surveyOptionMapper;

    @Autowired
    private SurveyOptionAnswerMapper surveyOptionAnswerMapper;

    @Autowired
    private SurveyQuestionAnswerMapper surveyQuestionAnswerMapper;

    @Autowired
    private SurveyUserQuestionnaireMapper surveyUserQuestionnaireMapper;

    /**
     * 查询问卷中心
     * 
     * @param questionnaireId 问卷中心主键
     * @return 问卷中心
     */
    @Override
    public SurveyQuestionnaire selectSurveyQuestionnaireByQuestionnaireId(String questionnaireId)
    {
        return surveyQuestionnaireMapper.selectSurveyQuestionnaireByQuestionnaireId(questionnaireId);
    }

    /**
     * 查询问卷中心列表
     * 
     * @param surveyQuestionnaire 问卷中心
     * @return 问卷中心
     */
    @Override
    public List<SurveyQuestionnaire> selectSurveyQuestionnaireList(SurveyQuestionnaire surveyQuestionnaire)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();
        Map<String,Object> params = new HashMap<>();
        params.put("userId",user.getUserId());
        params.put("questionnaireId",surveyQuestionnaire.getQuestionnaireId());
        params.put("questionnaireName",surveyQuestionnaire.getQuestionnaireName());
        params.put("status",surveyQuestionnaire.getStatus());
        return surveyQuestionnaireMapper.selectSurveyQuestionnaireList(params);
    }

    /**
     * 新增问卷中心
     * 
     * @param surveyQuestionnaire 问卷中心
     * @return 结果
     */
    @Override
    public int insertSurveyQuestionnaire(SurveyQuestionnaire surveyQuestionnaire)
    {
        surveyQuestionnaire.setCreateTime(DateUtils.getNowDate());
        surveyQuestionnaireMapper.insertSurveyQuestionnaire(surveyQuestionnaire);
        SurveyUserQuestionnaire surveyUserQuestionnaire = new SurveyUserQuestionnaire();
        surveyUserQuestionnaire.setQuestionnaire_id(new Long(surveyQuestionnaire.getQuestionnaireId()));
        surveyUserQuestionnaire.setUser_id(1L);
        return surveyUserQuestionnaireMapper.insert(surveyUserQuestionnaire);

    }

    /**
     * 修改问卷中心
     * 
     * @param surveyQuestionnaire 问卷中心
     * @return 结果
     */
    @Override
    public int updateSurveyQuestionnaire(SurveyQuestionnaire surveyQuestionnaire)
    {
        surveyQuestionnaire.setUpdateTime(DateUtils.getNowDate());
        return surveyQuestionnaireMapper.updateSurveyQuestionnaire(surveyQuestionnaire);
    }

    /**
     * 批量删除问卷中心
     * 
     * @param questionnaireIds 需要删除的问卷中心主键
     * @return 结果
     */
    @Override
    public int deleteSurveyQuestionnaireByQuestionnaireIds(String[] questionnaireIds)
    {
        for(String questionnaireId:questionnaireIds) {
            List<SurveyQuestionnaireQuestion> surveyQuestionnaireQuestions = surveyQuestionnaireQuestionMapper.selectListByQuestionnaireId(new Long(questionnaireId));
            for(SurveyQuestionnaireQuestion surveyQuestionnaireQuestion:surveyQuestionnaireQuestions){
                List<SurveyQuestionOption> surveyQuestionOptions = surveyQuestionOptionMapper.selectListById(surveyQuestionnaireQuestion.getQuestion_id());
                for(SurveyQuestionOption surveyQuestionOption:surveyQuestionOptions) {
                    surveyOptionMapper.deleteSurveyOptionByOptionId(String.valueOf(surveyQuestionOption.getOption_id()));
                    surveyQuestionOptionMapper.deleteByPrimaryKey(surveyQuestionOption.getQuestion_id());
                }
                surveyQuestionMapper.deleteSurveyQuestionByQuestionId(String.valueOf(surveyQuestionnaireQuestion.getQuestion_id()));
                surveyQuestionnaireQuestionMapper.deleteByPrimaryKey(surveyQuestionnaireQuestion.getQuestion_id());
            }
        }
            return surveyQuestionnaireMapper.deleteSurveyQuestionnaireByQuestionnaireIds(questionnaireIds);
    }

    /**
     * 删除问卷中心信息
     * 
     * @param questionnaireId 问卷中心主键
     * @return 结果
     */
    @Override
    public int deleteSurveyQuestionnaireByQuestionnaireId(String questionnaireId)
    {
        return surveyQuestionnaireMapper.deleteSurveyQuestionnaireByQuestionnaireId(questionnaireId);
    }

    @Override
    public int publishSurveyQuestionnaire(String questionnaireId) {
        return surveyQuestionnaireMapper.publishQuestionnaire(questionnaireId);
    }

    @Override
    public SurveyData getDataStats(String questionnaireId) {
        SurveyData surveyData = new SurveyData();
        SurveyQuestionnaire surveyQuestionnaire = surveyQuestionnaireMapper.selectSurveyQuestionnaireByQuestionnaireId(questionnaireId);

        surveyData.setQuestionnaireId(surveyQuestionnaire.getQuestionnaireId());
        surveyData.setQuestionnaireName(surveyQuestionnaire.getQuestionnaireName());
        surveyData.setStatus(surveyQuestionnaire.getStatus());
        surveyData.setCreateTime(surveyQuestionnaire.getCreateTime());
        surveyData.setParticipantCount(surveyQuestionnaire.getParticipantCount());
        surveyData.setQuestionCount(surveyQuestionnaire.getQuestionCount());

        List<SurveyQuestionnaireQuestion> surveyQuestionnaireQuestions = surveyQuestionnaireQuestionMapper.selectListByQuestionnaireId(Long.valueOf(questionnaireId));
        List<SurveyDataQuestion> surveyDataQuestionList = new ArrayList<>();
        for(SurveyQuestionnaireQuestion surveyQuestionnaireQuestion :surveyQuestionnaireQuestions){
            SurveyQuestion surveyQuestion = surveyQuestionMapper.selectSurveyQuestionByQuestionId(String.valueOf(surveyQuestionnaireQuestion.getQuestion_id()));
            List<SurveyQuestionOption> surveyQuestionOptions = surveyQuestionOptionMapper.selectListById(Long.valueOf(surveyQuestion.getQuestionId()));

            SurveyDataQuestion surveyDataQuestion = new SurveyDataQuestion();
            if(surveyQuestionOptions != null) {
                List<SurveyDataOption> surveyDataOptions = new ArrayList<>();
                for (SurveyQuestionOption surveyQuestionOption : surveyQuestionOptions) {
                    SurveyOption surveyOption = surveyOptionMapper.selectSurveyOptionByOptionId(String.valueOf(surveyQuestionOption.getOption_id()));
                    SurveyDataOption surveyDataOption = new SurveyDataOption();

                    surveyDataOption.setOptionId(surveyOption.getOptionId());
                    surveyDataOption.setOptionOrder(surveyOption.getOptionOrder());
                    surveyDataOption.setQuestionId(surveyOption.getQuestionId());
                    surveyDataOption.setOptionText(surveyOption.getOptionText());
                    surveyDataOption.setCreateTime(surveyOption.getCreateTime());

                    surveyDataOption.setAnCount(surveyOptionAnswerMapper.countAnswerByOptionId(surveyOption.getOptionId()));
                    surveyDataOptions.add(surveyDataOption);
                }
                surveyDataQuestion.setSurveyDataOptionList(surveyDataOptions);
            }

            surveyDataQuestion.setQuestionnaireId(surveyQuestion.getQuestionnaireId());
            surveyDataQuestion.setQuestionId(String.valueOf(surveyQuestion.getQuestionId()));
            surveyDataQuestion.setQuestionOrder(surveyQuestion.getQuestionOrder());
            surveyDataQuestion.setQuestionTitle(surveyQuestion.getQuestionTitle());
            surveyDataQuestion.setQuestionType(surveyQuestion.getQuestionType());
            surveyDataQuestion.setCreateBy(surveyQuestion.getCreateBy());
            surveyDataQuestion.setCreateTime(surveyQuestion.getCreateTime());
            surveyDataQuestion.setRecordCount(surveyQuestionAnswerMapper.countAnswerByQuestionId(String.valueOf(surveyQuestion.getQuestionId())));

            surveyDataQuestionList.add(surveyDataQuestion);
        }

        surveyData.setSurveyDataQuestionList(surveyDataQuestionList);
        return surveyData;
    }

    @Override
    public List<SysUser> selectAssignedList(SysUser user, String questionnaireId) {
        Map<String,Object> params = new HashMap<>();
        params.put("username",user.getUserName());
        params.put("phonenumber",user.getPhonenumber());
        params.put("questionnaireId",questionnaireId);
        return surveyQuestionnaireMapper.selectUserListByQuestionnaireId(params);
    }

    @Override
    public List<SysUser> selectUnAssignedList(SysUser user, String questionnaireId) {
        Map<String,Object> params = new HashMap<>();
        params.put("userName",user.getUserName());
        params.put("phonenumber",user.getPhonenumber());
        params.put("questionnaireId",questionnaireId);
        return surveyQuestionnaireMapper.selectUnAssignUserListByQuestionnaireId(params);
    }

    @Override
    public int insertAsUsers(String questionnaireId, String[] userIds) {
        List<SurveyUserQuestionnaire> surveyUserQuestionnaires = new ArrayList<>();
        for(String userId:userIds){
            SurveyUserQuestionnaire surveyUserQuestionnaire = new SurveyUserQuestionnaire();
            surveyUserQuestionnaire.setUser_id(Long.valueOf(userId));
            surveyUserQuestionnaire.setQuestionnaire_id(Long.valueOf(questionnaireId));
            surveyUserQuestionnaires.add(surveyUserQuestionnaire);
        }
        return surveyUserQuestionnaireMapper.batchUserQuestionnaire(surveyUserQuestionnaires);
    }

    @Override
    public int deleteAsUser(SurveyUserQuestionnaire userQuestionnaire) {

        return surveyUserQuestionnaireMapper.deleteByPrimaryKey(userQuestionnaire);
    }

    @Override
    public int deleteAsUsers(String questionnaireId, String[] userIds) {

        return surveyUserQuestionnaireMapper.deleteAsUsers(questionnaireId,userIds);
    }

    @Override
    public int updateSurveyQuestionCount(String questionnaireId, String questionCount) {

        return surveyQuestionnaireMapper.updateSurveyQuestionCount(questionnaireId,questionCount);
    }

    @Override
    public int updateSurveyQuestionnaireStatus(String questionnaireId, String status) {
        Map<String,Object> params = new HashMap<>();
        params.put("questionnaireId",questionnaireId);
        params.put("status",status);
        return surveyQuestionnaireMapper.updateSurveyQuestionnaireStatus(params);
    }

}
