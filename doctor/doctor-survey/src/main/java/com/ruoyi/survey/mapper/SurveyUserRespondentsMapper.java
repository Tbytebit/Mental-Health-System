package com.doctor.survey.mapper;

import com.doctor.survey.domain.SurveyUserRespondents;

/**
* @author 果
* @description 针对表【survey_user_respondents(用户与答卷关联表)】的数据库操作Mapper
* @createDate 2024-01-26 10:10:40
* @Entity com.doctor.survey.domain.SurveyUserRespondents
*/
public interface SurveyUserRespondentsMapper {

    int deleteByPrimaryKey(Long id);

    int insert(SurveyUserRespondents record);

    int insertSelective(SurveyUserRespondents record);

    SurveyUserRespondents selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SurveyUserRespondents record);

    int updateByPrimaryKey(SurveyUserRespondents record);

    String getUserIdByRespondentsId(String respondentsId);
}
