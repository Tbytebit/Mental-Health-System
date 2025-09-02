package com.doctor.ai.domain;

import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * AI助手对象 ai_assistant
 */
public class AiAssistant extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 助手ID */
    private Long assistantId;

    /** 助手名称 */
    @Excel(name = "助手名称")
    private String name;

    /** 助手描述 */
    @Excel(name = "助手描述")
    private String description;

    /** 助手头像 */
    @Excel(name = "助手头像")
    private String avatar;

    /** 助手类型 */
    @Excel(name = "助手类型")
    private Integer assistantType;

    /** 系统提示词 */
    @Excel(name = "系统提示词")
    private String systemPrompt;

    /** 排序 */
    @Excel(name = "排序")
    private Integer orderNum;

    /** 状态（0正常 1停用） */
    @Excel(name = "状态", readConverterExp = "0=正常,1=停用")
    private String status;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public Long getAssistantId() {
        return assistantId;
    }

    public void setAssistantId(Long assistantId) {
        this.assistantId = assistantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public Integer getAssistantType() {
        return assistantType;
    }

    public void setAssistantType(Integer assistantType) {
        this.assistantType = assistantType;
    }

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public void setSystemPrompt(String systemPrompt) {
        this.systemPrompt = systemPrompt;
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
                .append("assistantId", getAssistantId())
                .append("name", getName())
                .append("description", getDescription())
                .append("avatar", getAvatar())
                .append("assistantType", getAssistantType())
                .append("systemPrompt", getSystemPrompt())
                .append("orderNum", getOrderNum())
                .append("status", getStatus())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
} 