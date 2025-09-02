package com.doctor.survey.domain;

import java.util.List;

public class SurveyInfoQuestion extends SurveyQuestion{
    List<SurveyOption> optionList;

    String answers;

    public List<SurveyOption> getOptionList() {
        return optionList;
    }

    public void setOptionList(List<SurveyOption> optionList) {
        this.optionList = optionList;
    }


    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }
}
