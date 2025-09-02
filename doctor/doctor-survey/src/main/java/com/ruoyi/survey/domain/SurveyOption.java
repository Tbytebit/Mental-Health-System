package com.doctor.survey.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;

/**
 * 调查问卷问题选项存储对象 survey_option
 * 
 * @author ruoyi
 * @date 2024-01-19
 */
public class SurveyOption extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private String optionId;

    /** 问题id */
    @Excel(name = "问题id")
    private String questionId;

    /** 问题选项文本 */
    @Excel(name = "问题选项文本")
    private String optionText;

    /** 问题选项排序 */
    @Excel(name = "问题选项排序")
    private String optionOrder;

    public void setOptionId(String optionId) 
    {
        this.optionId = optionId;
    }

    public String getOptionId() 
    {
        return optionId;
    }
    public void setQuestionId(String questionId) 
    {
        this.questionId = questionId;
    }

    public String getQuestionId() 
    {
        return questionId;
    }
    public void setOptionText(String optionText) 
    {
        this.optionText = optionText;
    }

    public String getOptionText() 
    {
        return optionText;
    }
    public void setOptionOrder(String optionOrder) 
    {
        this.optionOrder = optionOrder;
    }

    public String getOptionOrder() 
    {
        return optionOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("optionId", getOptionId())
            .append("questionId", getQuestionId())
            .append("optionText", getOptionText())
            .append("optionOrder", getOptionOrder())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
