package com.doctor.survey.mapper;

import com.doctor.survey.domain.SurveyOptionAnswer;

import java.util.List;

/**
* @author 果
* @description 针对表【survey_option_answer(选项与答案关联表)】的数据库操作Mapper
* @createDate 2024-01-23 10:00:13
* @Entity com.doctor.survey.domain.SurveyOptionAnswer
*/
public interface SurveyOptionAnswerMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SurveyOptionAnswer record);

    int insertSelective(SurveyOptionAnswer record);

    SurveyOptionAnswer selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SurveyOptionAnswer record);

    int updateByPrimaryKey(SurveyOptionAnswer record);

    int deleteByAnswerIds(String[] answersIds);

    String countAnswerByOptionId(String optionId);
}
