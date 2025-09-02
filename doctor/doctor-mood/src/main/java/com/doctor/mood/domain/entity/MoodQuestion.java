package com.doctor.mood.domain.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;

/**
 * 心理量表问题对象 mood_question
 */
public class MoodQuestion extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 问题ID */
    private Long questionId;

    /** 量表ID */
    @Excel(name = "量表ID")
    private Long scaleId;

    /** 问题内容 */
    @Excel(name = "问题内容")
    private String questionContent;

    /** 问题类型（1=单选, 2=多选, 3=填空） */
    @Excel(name = "问题类型", readConverterExp = "1=单选,2=多选,3=填空")
    private String questionType;

    /** 问题选项（JSON格式存储） */
    private String questionOptions;

    /** 问题分值规则（JSON格式存储） */
    private String scoreRules;

    /** 显示顺序 */
    @Excel(name = "显示顺序")
    private Integer orderNum;

    /** 状态（0=停用,1=正常） */
    @Excel(name = "状态", readConverterExp = "0=停用,1=正常")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public Long getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }

    public Long getScaleId() {
        return scaleId;
    }

    public void setScaleId(Long scaleId) {
        this.scaleId = scaleId;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionOptions() {
        return questionOptions;
    }

    public void setQuestionOptions(String questionOptions) {
        this.questionOptions = questionOptions;
    }

    public String getScoreRules() {
        return scoreRules;
    }

    public void setScoreRules(String scoreRules) {
        this.scoreRules = scoreRules;
    }

    public Integer getOrderNum() {
        return orderNum;
    }

    public void setOrderNum(Integer orderNum) {
        this.orderNum = orderNum;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("questionId", getQuestionId())
            .append("scaleId", getScaleId())
            .append("questionContent", getQuestionContent())
            .append("questionType", getQuestionType())
            .append("questionOptions", getQuestionOptions())
            .append("scoreRules", getScoreRules())
            .append("orderNum", getOrderNum())
            .append("status", getStatus())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
} 