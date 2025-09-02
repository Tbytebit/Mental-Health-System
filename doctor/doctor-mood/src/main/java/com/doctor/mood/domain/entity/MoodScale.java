package com.doctor.mood.domain.entity;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;

/**
 * 心理情绪量表对象 mood_scale
 * 
 */
public class MoodScale extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 量表ID */
    private Long scaleId;

    /** 量表名称 */
    @Excel(name = "量表名称")
    private String scaleName;

    /** 量表描述 */
    @Excel(name = "量表描述")
    private String scaleDescription;

    /** 量表类型（1=情绪评估, 2=心理测试, 3=状态检测） */
    @Excel(name = "量表类型", readConverterExp = "1=情绪评估,2=心理测试,3=状态检测")
    private String scaleType;

    /** 状态（0=停用,1=正常） */
    @Excel(name = "状态", readConverterExp = "0=停用,1=正常")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public Long getScaleId() {
        return scaleId;
    }

    public void setScaleId(Long scaleId) {
        this.scaleId = scaleId;
    }

    public String getScaleName() {
        return scaleName;
    }

    public void setScaleName(String scaleName) {
        this.scaleName = scaleName;
    }

    public String getScaleDescription() {
        return scaleDescription;
    }

    public void setScaleDescription(String scaleDescription) {
        this.scaleDescription = scaleDescription;
    }

    public String getScaleType() {
        return scaleType;
    }

    public void setScaleType(String scaleType) {
        this.scaleType = scaleType;
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
            .append("scaleId", getScaleId())
            .append("scaleName", getScaleName())
            .append("scaleDescription", getScaleDescription())
            .append("scaleType", getScaleType())
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