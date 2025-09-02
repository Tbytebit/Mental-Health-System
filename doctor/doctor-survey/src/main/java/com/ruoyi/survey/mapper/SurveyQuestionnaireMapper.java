package com.doctor.survey.mapper;

import java.util.List;
import java.util.Map;

import com.doctor.common.core.domain.entity.SysUser;
import com.doctor.common.core.domain.entity.SysUser;
import com.doctor.survey.domain.SurveyQuestionnaire;
import io.lettuce.core.dynamic.annotation.Param;

/**
 * 问卷中心Mapper接口
 * 
 * @author guo
 * @date 2024-01-19
 */
public interface SurveyQuestionnaireMapper 
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
    public List<SurveyQuestionnaire> selectSurveyQuestionnaireList(Map<String, Object> surveyQuestionnaire);

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
     * 删除问卷中心
     * 
     * @param questionnaireId 问卷中心主键
     * @return 结果
     */
    public int deleteSurveyQuestionnaireByQuestionnaireId(String questionnaireId);

    /**
     * 批量删除问卷中心
     * 
     * @param questionnaireIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSurveyQuestionnaireByQuestionnaireIds(String[] questionnaireIds);

    public int publishQuestionnaire(String questionnaireId);

    List<SysUser> selectUserListByQuestionnaireId(Map<String,Object> params);

    List<SysUser> selectUnAssignUserListByQuestionnaireId(Map<String, Object> params);

    int updateSurveyQuestionCount(@Param("questionnaireId") String questionnaireId, @Param("questionCount") String questionCount);

    void updateQuestionCount(String questionnaireId);

    void deQuestionnaireCount(String questionnaireId);

    void updateSurveyRespondentsCount(String questionnaireId);

    void deSurveyRespondentsCount(Long questionnaireId);


    int updateSurveyQuestionnaireStatus(Map<String, Object> params);
}
