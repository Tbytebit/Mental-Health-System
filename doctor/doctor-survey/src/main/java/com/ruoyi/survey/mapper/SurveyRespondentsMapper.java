package com.doctor.survey.mapper;

import java.util.List;
import com.doctor.survey.domain.SurveyRespondents;

/**
 * 问卷答卷存储Mapper接口
 * 
 * @author guo
 * @date 2024-01-19
 */
public interface SurveyRespondentsMapper 
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
     * 删除问卷答卷存储
     * 
     * @param respondentsId 问卷答卷存储主键
     * @return 结果
     */
    public int deleteSurveyRespondentsByRespondentsId(String respondentsId);

    /**
     * 批量删除问卷答卷存储
     * 
     * @param respondentsIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteSurveyRespondentsByRespondentsIds(String[] respondentsIds);

    List<SurveyRespondents> selectSurveyRespondentsByRespondentsIds(String[] respondentsIds);
}
