package com.doctor.survey.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 问卷与答卷关联表
 * @TableName survey_questionnaire_respondents
 */
@TableName(value ="survey_questionnaire_respondents")
@Data
public class SurveyQuestionnaireRespondents implements Serializable {
    /**
     * 问卷ID
     */
    @TableId(value = "questionnaire_id")
    private Long questionnaire_id;

    /**
     * 答卷ID
     */
    @TableId(value = "respondents_id")
    private Long respondents_id;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        SurveyQuestionnaireRespondents other = (SurveyQuestionnaireRespondents) that;
        return (this.getQuestionnaire_id() == null ? other.getQuestionnaire_id() == null : this.getQuestionnaire_id().equals(other.getQuestionnaire_id()))
            && (this.getRespondents_id() == null ? other.getRespondents_id() == null : this.getRespondents_id().equals(other.getRespondents_id()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getQuestionnaire_id() == null) ? 0 : getQuestionnaire_id().hashCode());
        result = prime * result + ((getRespondents_id() == null) ? 0 : getRespondents_id().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", questionnaire_id=").append(questionnaire_id);
        sb.append(", respondents_id=").append(respondents_id);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}