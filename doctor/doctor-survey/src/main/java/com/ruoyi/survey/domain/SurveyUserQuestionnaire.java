package com.doctor.survey.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 用户与问卷关联表
 * @TableName survey_user_questionnaire
 */
@TableName(value ="survey_user_questionnaire")
@Data
public class SurveyUserQuestionnaire implements Serializable {
    /**
     * 用户ID
     */
    @TableId(value = "user_id")
    private Long user_id;

    /**
     * 问卷ID
     */
    @TableId(value = "questionnaire_id")
    private Long questionnaire_id;

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
        SurveyUserQuestionnaire other = (SurveyUserQuestionnaire) that;
        return (this.getUser_id() == null ? other.getUser_id() == null : this.getUser_id().equals(other.getUser_id()))
            && (this.getQuestionnaire_id() == null ? other.getQuestionnaire_id() == null : this.getQuestionnaire_id().equals(other.getQuestionnaire_id()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUser_id() == null) ? 0 : getUser_id().hashCode());
        result = prime * result + ((getQuestionnaire_id() == null) ? 0 : getQuestionnaire_id().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", user_id=").append(user_id);
        sb.append(", questionnaire_id=").append(questionnaire_id);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }

    public Long getUser_id() {
        return user_id;
    }

    public void setUser_id(Long user_id) {
        this.user_id = user_id;
    }

    public Long getQuestionnaire_id() {
        return questionnaire_id;
    }

    public void setQuestionnaire_id(Long questionnaire_id) {
        this.questionnaire_id = questionnaire_id;
    }
}