package com.doctor.survey.mapper;

import com.doctor.survey.domain.SurveyUserQuestionnaire;
import io.lettuce.core.dynamic.annotation.Param;

import java.util.List;

/**
* @author 果
* @description 针对表【survey_user_questionnaire(用户与问卷关联表)】的数据库操作Mapper
* @createDate 2024-01-25 14:06:16
* @Entity com.doctor.survey.domain.SurveyUserQuestionnaire
*/
public interface SurveyUserQuestionnaireMapper {

    int deleteByPrimaryKey(SurveyUserQuestionnaire record);

    int insert(SurveyUserQuestionnaire record);

    int insertSelective(SurveyUserQuestionnaire record);

    SurveyUserQuestionnaire selectByPrimaryKey(Long id);

    int updateByPrimaryKeySelective(SurveyUserQuestionnaire record);

    int updateByPrimaryKey(SurveyUserQuestionnaire record);

    List<String> getUserIdsByQuestionnaireId(String questionnaireId);

    int batchUserQuestionnaire(List<SurveyUserQuestionnaire> surveyUserQuestionnaires);


    int deleteAsUsers(@Param("questionnaireId") String questionnaireId, @Param("userIds")String[] userIds);
}
