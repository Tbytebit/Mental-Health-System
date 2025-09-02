package com.doctor.survey.mapper;

import com.doctor.survey.domain.SurveyQuestionnaireQuestion;

import java.util.List;

/**
* @author 果
* @description 针对表【survey_questionnaire_question(问卷与问题关联表)】的数据库操作Mapper
* @createDate 2024-01-21 21:34:10
* @Entity com.doctor.survey.domain.SurveyQuestionnaireQuestion
*/
public interface SurveyQuestionnaireQuestionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SurveyQuestionnaireQuestion record);

    int insertSelective(SurveyQuestionnaireQuestion record);

    SurveyQuestionnaireQuestion selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SurveyQuestionnaireQuestion record);

    int updateByPrimaryKey(SurveyQuestionnaireQuestion record);

    List<SurveyQuestionnaireQuestion> selectListByQuestionnaireId(Long id);

    String getQuestionnaireId(String questionId);
}
