package com.doctor.survey.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 选项与答案关联表
 * @TableName survey_option_answer
 */
@TableName(value ="survey_option_answer")
@Data
public class SurveyOptionAnswer implements Serializable {
    /**
     * 选项ID
     */
    @TableId(value = "option_id")
    private Long option_id;

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
        SurveyOptionAnswer other = (SurveyOptionAnswer) that;
        return (this.getOption_id() == null ? other.getOption_id() == null : this.getOption_id().equals(other.getOption_id()))
            && (this.getAnswer_id() == null ? other.getAnswer_id() == null : this.getAnswer_id().equals(other.getAnswer_id()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getOption_id() == null) ? 0 : getOption_id().hashCode());
        result = prime * result + ((getAnswer_id() == null) ? 0 : getAnswer_id().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", option_id=").append(option_id);
        sb.append(", answer_id=").append(answer_id);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}