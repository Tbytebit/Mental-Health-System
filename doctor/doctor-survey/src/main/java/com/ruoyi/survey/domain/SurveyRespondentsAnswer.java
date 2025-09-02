package com.doctor.survey.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 答卷与答案关联表
 * @TableName survey_respondents_answer
 */
@TableName(value ="survey_respondents_answer")
@Data
public class SurveyRespondentsAnswer implements Serializable {
    /**
     * 答卷ID
     */
    @TableId(value = "respondents_id")
    private Long respondents_id;

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
        SurveyRespondentsAnswer other = (SurveyRespondentsAnswer) that;
        return (this.getRespondents_id() == null ? other.getRespondents_id() == null : this.getRespondents_id().equals(other.getRespondents_id()))
            && (this.getAnswer_id() == null ? other.getAnswer_id() == null : this.getAnswer_id().equals(other.getAnswer_id()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getRespondents_id() == null) ? 0 : getRespondents_id().hashCode());
        result = prime * result + ((getAnswer_id() == null) ? 0 : getAnswer_id().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", respondents_id=").append(respondents_id);
        sb.append(", answer_id=").append(answer_id);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}