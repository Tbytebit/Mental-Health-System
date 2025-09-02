package com.doctor.survey.domain;

import java.util.List;

public class SurveyInfo extends SurveyRespondents{

    private List<SurveyInfoQuestion> surveyInfoQuestionList;

    public List<SurveyInfoQuestion> getSurveyInfoQuestionList() {
        return surveyInfoQuestionList;
    }

    public void setSurveyInfoQuestionList(List<SurveyInfoQuestion> surveyInfoQuestionList) {
        this.surveyInfoQuestionList = surveyInfoQuestionList;
    }

}
