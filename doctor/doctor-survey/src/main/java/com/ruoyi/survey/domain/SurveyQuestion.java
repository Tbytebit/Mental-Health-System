package com.doctor.survey.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;

/**
 * 调查问卷问题，用于存储问卷中的问题对象 survey_question
 * 
 * @author ruoyi
 * @date 2024-01-19
 */
public class SurveyQuestion extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private String questionId;

    /** 所属问卷id */
    @Excel(name = "所属问卷id")
    private Long questionnaireId;

    /** 问题类型 */
    @Excel(name = "问题类型")
    private String questionType;

    /** 问题题目 */
    @Excel(name = "问题题目")
    private String questionTitle;

    /** 问题在问卷中的顺序 */
    @Excel(name = "问题在问卷中的顺序")
    private Long questionOrder;

    public void setQuestionId(String questionId) 
    {
        this.questionId = questionId;
    }

    public String getQuestionId()
    {
        return questionId;
    }
    public void setQuestionnaireId(Long questionnaireId) 
    {
        this.questionnaireId = questionnaireId;
    }

    public Long getQuestionnaireId() 
    {
        return questionnaireId;
    }
    public void setQuestionType(String questionType)
    {
        this.questionType = questionType;
    }

    public String getQuestionType()
    {
        return questionType;
    }
    public void setQuestionTitle(String questionTitle) 
    {
        this.questionTitle = questionTitle;
    }

    public String getQuestionTitle() 
    {
        return questionTitle;
    }
    public void setQuestionOrder(Long questionOrder) 
    {
        this.questionOrder = questionOrder;
    }

    public Long getQuestionOrder() 
    {
        return questionOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("questionId", getQuestionId())
            .append("questionnaireId", getQuestionnaireId())
            .append("questionType", getQuestionType())
            .append("questionTitle", getQuestionTitle())
            .append("questionOrder", getQuestionOrder())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
