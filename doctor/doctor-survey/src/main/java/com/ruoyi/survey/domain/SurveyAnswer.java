package com.doctor.survey.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;

/**
 * 答卷的问题答案存储对象 survey_answer
 * 
 * @author ruoyi
 * @date 2024-01-19
 */
public class SurveyAnswer extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private String answerId;

    /** 答卷id */
    @Excel(name = "答卷id")
    private String respondentsId;

    /** 问题id，对应于questionnaire_question表的id字段 */
    @Excel(name = "问题id，对应于questionnaire_question表的id字段")
    private String questionId;

    /** 问题答案 */
    @Excel(name = "问题答案")
    private String answer;

    public String getAnswerType() {
        return answerType;
    }

    public void setAnswerType(String answerType) {
        this.answerType = answerType;
    }

    private String answerType;

    public void setAnswerId(String answerId) 
    {
        this.answerId = answerId;
    }

    public String getAnswerId() 
    {
        return answerId;
    }
    public void setRespondentsId(String respondentsId) 
    {
        this.respondentsId = respondentsId;
    }

    public String getRespondentsId() 
    {
        return respondentsId;
    }
    public void setQuestionId(String questionId) 
    {
        this.questionId = questionId;
    }

    public String getQuestionId() 
    {
        return questionId;
    }
    public void setAnswer(String answer) 
    {
        this.answer = answer;
    }

    public String getAnswer() 
    {
        return answer;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("answerId", getAnswerId())
            .append("respondentsId", getRespondentsId())
            .append("questionId", getQuestionId())
            .append("answer", getAnswer())
            .toString();
    }
}
