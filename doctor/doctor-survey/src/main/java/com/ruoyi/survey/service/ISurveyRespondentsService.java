package com.doctor.survey.service;

import java.util.List;

import com.doctor.common.core.domain.entity.SysUser;
import com.doctor.survey.domain.SurveyInfo;
import com.doctor.survey.domain.SurveyRespondents;

/**
 * 问卷答卷存储Service接口
 * 
 * @author guo
 * @date 2024-01-19
 */
public interface ISurveyRespondentsService 
{
    /**
     * 查询问卷答卷存储
     * 
     * @param respondentsId 问卷答卷存储主键
     * @return 问卷答卷存储
     */
    public SurveyRespondents selectSurveyRespondentsByRespondentsId(String respondentsId);

    /**
     * 查询问卷答卷存储列表
     * 
     * @param surveyRespondents 问卷答卷存储
     * @return 问卷答卷存储集合
     */
    public List<SurveyRespondents> selectSurveyRespondentsList(SurveyRespondents surveyRespondents);

    /**
     * 新增问卷答卷存储
     * 
     * @param surveyRespondents 问卷答卷存储
     * @return 结果
     */
    public int insertSurveyRespondents(SurveyRespondents surveyRespondents);

    /**
     * 修改问卷答卷存储
     * 
     * @param surveyRespondents 问卷答卷存储
     * @return 结果
     */
    public int updateSurveyRespondents(SurveyRespondents surveyRespondents);

    /**
     * 批量删除问卷答卷存储
     * 
     * @param respondentsIds 需要删除的问卷答卷存储主键集合
     * @return 结果
     */
    public int deleteSurveyRespondentsByRespondentsIds(String[] respondentsIds);

    /**
     * 删除问卷答卷存储信息
     * 
     * @param respondentsId 问卷答卷存储主键
     * @return 结果
     */
    int deleteSurveyRespondentsByRespondentsId(String respondentsId);

    List<SurveyRespondents> selectSurveyRespondentsListByQuestionnaireId(String questionnaireId);

    SurveyInfo selectSurveyInfoByRespondentsId(String respondentsId);
}
