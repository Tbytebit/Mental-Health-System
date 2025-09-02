package com.doctor.survey.mapper;

import com.doctor.survey.domain.SurveyQuestionnaireRespondents;

/**
* @author 果
* @description 针对表【survey_questionnaire_respondents(问卷与答卷关联表)】的数据库操作Mapper
* @createDate 2024-01-23 09:15:44
* @Entity com.doctor.survey.domain.SurveyQuestionnaireRespondents
*/
public interface SurveyQuestionnaireRespondentsMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SurveyQuestionnaireRespondents record);

    int insertSelective(SurveyQuestionnaireRespondents record);

    SurveyQuestionnaireRespondents selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SurveyQuestionnaireRespondents record);

    int updateByPrimaryKey(SurveyQuestionnaireRespondents record);

    int deleteByRespondentsIds(String[] respondentsIds);

    String[] selectByQuestionnaireId(String questionnaireId);

    SurveyQuestionnaireRespondents selectByRespondentsId(String RespondentsId);
}
