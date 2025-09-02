package com.doctor.survey.mapper;

import com.doctor.survey.domain.SurveyQuestionOption;

import java.util.List;

/**
* @author 果
* @description 针对表【survey_question_option(问题与选项关联表)】的数据库操作Mapper
* @createDate 2024-01-21 21:33:48
* @Entity com.doctor.survey.domain.SurveyQuestionOption
*/
public interface SurveyQuestionOptionMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SurveyQuestionOption record);

    int insertSelective(SurveyQuestionOption record);

    SurveyQuestionOption selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SurveyQuestionOption record);

    int updateByPrimaryKey(SurveyQuestionOption record);

    List<SurveyQuestionOption> selectListById(Long questionId);

    void deleteByOptionId(Long optionId);
}
