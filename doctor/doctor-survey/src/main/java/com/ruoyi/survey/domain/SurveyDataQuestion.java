package com.doctor.survey.domain;
import java.util.List;
public class SurveyDataQuestion extends SurveyQuestion{

    private List<SurveyDataOption> surveyDataOptionList;

    private String recordCount;

    public List<SurveyDataOption> getSurveyDataOptionList() {
        return surveyDataOptionList;
    }

    public void setSurveyDataOptionList(List<SurveyDataOption> surveyDataOptionList) {
        this.surveyDataOptionList = surveyDataOptionList;
    }

    public String getRecordCount() {
        return recordCount;
    }

    public void setRecordCount(String recordCount) {
        this.recordCount = recordCount;
    }
}
