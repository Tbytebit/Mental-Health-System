package com.doctor.survey.mapper;

import com.doctor.survey.domain.SurveyQuestionAnswer;

/**
* @author 果
* @description 针对表【survey_question_answer(问题与答案关联表)】的数据库操作Mapper
* @createDate 2024-01-23 10:19:42
* @Entity com.doctor.survey.domain.SurveyQuestionAnswer
*/
public interface SurveyQuestionAnswerMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SurveyQuestionAnswer record);

    int insertSelective(SurveyQuestionAnswer record);

    SurveyQuestionAnswer selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SurveyQuestionAnswer record);

    int updateByPrimaryKey(SurveyQuestionAnswer record);

    int deleteByAnswerIds(String[] answerIds);

    String countAnswerByQuestionId(String QuestionId);
}
