package com.doctor.survey.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 问题与答案关联表
 * @TableName survey_question_answer
 */
@TableName(value ="survey_question_answer")
@Data
public class SurveyQuestionAnswer implements Serializable {
    /**
     * 问题ID
     */
    @TableId(value = "question_id")
    private Long question_id;

    /**
     * 答案ID
     */
    @TableId(value = "answer_id")
    private Long answer_id;

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
        SurveyQuestionAnswer other = (SurveyQuestionAnswer) that;
        return (this.getQuestion_id() == null ? other.getQuestion_id() == null : this.getQuestion_id().equals(other.getQuestion_id()))
            && (this.getAnswer_id() == null ? other.getAnswer_id() == null : this.getAnswer_id().equals(other.getAnswer_id()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getQuestion_id() == null) ? 0 : getQuestion_id().hashCode());
        result = prime * result + ((getAnswer_id() == null) ? 0 : getAnswer_id().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", question_id=").append(question_id);
        sb.append(", answer_id=").append(answer_id);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}