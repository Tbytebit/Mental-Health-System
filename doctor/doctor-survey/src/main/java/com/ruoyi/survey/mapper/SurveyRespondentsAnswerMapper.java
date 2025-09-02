package com.doctor.survey.mapper;

import com.doctor.survey.domain.SurveyRespondentsAnswer;

import java.util.List;

/**
* @author 果
* @description 针对表【survey_respondents_answer(答卷与答案关联表)】的数据库操作Mapper
* @createDate 2024-01-23 09:36:33
* @Entity com.doctor.survey.domain.SurveyRespondentsAnswer
*/
public interface SurveyRespondentsAnswerMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SurveyRespondentsAnswer record);

    int insertSelective(SurveyRespondentsAnswer record);

    SurveyRespondentsAnswer selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SurveyRespondentsAnswer record);

    int updateByPrimaryKey(SurveyRespondentsAnswer record);
    String[] selectByRespondentsIds(String[] respondentsIds);

    int deleteByRespondentsIds(String[] respondentsIds);
}
