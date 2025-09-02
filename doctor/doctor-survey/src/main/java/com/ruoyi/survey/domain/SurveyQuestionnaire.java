package com.doctor.survey.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;

/**
 * 问卷中心对象 survey_questionnaire
 * 
 * @author guo
 * @date 2024-01-19
 */
@Setter
@Getter
public class SurveyQuestionnaire extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private String questionnaireId;

    /** 调查问卷名称 */
    @Excel(name = "调查问卷名称")
    private String questionnaireName;

    /** 问卷发布时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "问卷发布时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date publishDate;

    /** 该问卷问题总数 */
    @Excel(name = "该问卷问题总数")
    private String questionCount;

    /** 问卷参与人数 */
    @Excel(name = "问卷参与人数")
    private Long participantCount;

    /** 该问卷的状态，0设计中，1问卷已发布信息收集中，2问卷结束 */
    @Excel(name = "该问卷的状态，0设计中，1问卷已发布信息收集中，2问卷结束")
    private Long status;

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("questionnaireId", getQuestionnaireId())
            .append("questionnaireName", getQuestionnaireName())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("publishDate", getPublishDate())
            .append("questionCount", getQuestionCount())
            .append("participantCount", getParticipantCount())
            .append("status", getStatus())
            .toString();
    }
}
