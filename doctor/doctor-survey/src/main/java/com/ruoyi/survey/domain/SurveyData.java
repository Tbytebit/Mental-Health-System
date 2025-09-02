package com.doctor.survey.domain;

import java.util.List;

public class SurveyData extends SurveyQuestionnaire{

    private List<SurveyDataQuestion> surveyDataQuestionList;



    public List<SurveyDataQuestion> getSurveyDataQuestionList() {
        return surveyDataQuestionList;
    }

    public void setSurveyDataQuestionList(List<SurveyDataQuestion> surveyDataQuestionList) {
        this.surveyDataQuestionList = surveyDataQuestionList;
    }
}
