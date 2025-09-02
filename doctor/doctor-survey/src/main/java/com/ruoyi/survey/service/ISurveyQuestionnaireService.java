package com.doctor.survey.service;

import java.util.List;

import com.doctor.common.core.domain.entity.SysUser;
import com.doctor.survey.domain.SurveyData;
import com.doctor.survey.domain.SurveyQuestionnaire;
import com.doctor.survey.domain.SurveyUserQuestionnaire;

/**
 * 问卷中心Service接口
 * 
 * @author guo
 * @date 2024-01-19
 */
public interface ISurveyQuestionnaireService 
{
    /**
     * 查询问卷中心
     * 
     * @param questionnaireId 问卷中心主键
     * @return 问卷中心
     */
    public SurveyQuestionnaire selectSurveyQuestionnaireByQuestionnaireId(String questionnaireId);

    /**
     * 查询问卷中心列表
     * 
     * @param surveyQuestionnaire 问卷中心
     * @return 问卷中心集合
     */
    public List<SurveyQuestionnaire> selectSurveyQuestionnaireList(SurveyQuestionnaire surveyQuestionnaire);

    /**
     * 新增问卷中心
     * 
     * @param surveyQuestionnaire 问卷中心
     * @return 结果
     */
    public int insertSurveyQuestionnaire(SurveyQuestionnaire surveyQuestionnaire);

    /**
     * 修改问卷中心
     * 
     * @param surveyQuestionnaire 问卷中心
     * @return 结果
     */
    public int updateSurveyQuestionnaire(SurveyQuestionnaire surveyQuestionnaire);

    /**
     * 批量删除问卷中心
     * 
     * @param questionnaireIds 需要删除的问卷中心主键集合
     * @return 结果
     */
    public int deleteSurveyQuestionnaireByQuestionnaireIds(String[] questionnaireIds);

    /**
     * 删除问卷中心信息
     * 
     * @param questionnaireId 问卷中心主键
     * @return 结果
     */
    public int deleteSurveyQuestionnaireByQuestionnaireId(String questionnaireId);

    int publishSurveyQuestionnaire(String questionnaireId);

    SurveyData getDataStats(String questionnaireId);

    List<SysUser> selectAssignedList(SysUser user, String questionnaireId);

    List<SysUser> selectUnAssignedList(SysUser user, String questionnaireId);

    int insertAsUsers(String questionnaireId, String[] userIds);


    int deleteAsUser(SurveyUserQuestionnaire userQuestionnaire);

    int deleteAsUsers(String questionnaireId, String[] userIds);

    int updateSurveyQuestionCount(String questionnaireId, String questionCount);

    int updateSurveyQuestionnaireStatus(String questionnaireId, String status);
}
