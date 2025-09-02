package com.doctor.survey.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 用户与答卷关联表
 * @TableName survey_user_respondents
 */
@TableName(value ="survey_user_respondents")
@Data
public class SurveyUserRespondents implements Serializable {
    /**
     * 用户ID
     */
    @TableId(value = "user_id")
    private Long user_id;

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
        SurveyUserRespondents other = (SurveyUserRespondents) that;
        return (this.getUser_id() == null ? other.getUser_id() == null : this.getUser_id().equals(other.getUser_id()))
            && (this.getRespondents_id() == null ? other.getRespondents_id() == null : this.getRespondents_id().equals(other.getRespondents_id()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getUser_id() == null) ? 0 : getUser_id().hashCode());
        result = prime * result + ((getRespondents_id() == null) ? 0 : getRespondents_id().hashCode());
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getClass().getSimpleName());
        sb.append(" [");
        sb.append("Hash = ").append(hashCode());
        sb.append(", user_id=").append(user_id);
        sb.append(", respondents_id=").append(respondents_id);
        sb.append(", serialVersionUID=").append(serialVersionUID);
        sb.append("]");
        return sb.toString();
    }
}