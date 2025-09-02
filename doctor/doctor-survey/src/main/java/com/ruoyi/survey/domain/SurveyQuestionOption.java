package com.doctor.survey.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 问题与选项关联表
 * @TableName survey_question_option
 */
@TableName(value ="survey_question_option")
@Data
public class SurveyQuestionOption implements Serializable {
    /**
     * 问题ID
     */
    @TableId(value = "question_id")
    private Long question_id;

    /**
     * 选项ID
     */
    @TableId(value = "option_id")
    private Long option_id;

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
        SurveyQuestionOption other = (SurveyQuestionOption) that;
        return (this.getQuestion_id() == null ? other.getQuestion_id() == null : this.getQuestion_id().equals(other.getQuestion_id()))
            && (this.getOption_id() == null ? other.getOption_id() == null : this.getOption_id().equals(other.getOption_id()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getQuestion_id() == null) ? 0 : getQuestion_id().hashCode());
        result = prime * result + ((getOption_id() == null) ? 0 : getOption_id().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", question_id=").append(question_id);
        sb.append(", option_id=").append(option_id);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}