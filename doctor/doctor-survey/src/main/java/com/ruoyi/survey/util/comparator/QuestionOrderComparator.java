package com.doctor.survey.util.comparator;

import com.doctor.survey.domain.SurveyQuestion;

;import java.util.Comparator;

public class QuestionOrderComparator implements Comparator<SurveyQuestion> {
    @Override
    public int compare(SurveyQuestion q1, SurveyQuestion q2) {
        return (int) (q1.getQuestionOrder() - q2.getQuestionOrder());
    }
}
