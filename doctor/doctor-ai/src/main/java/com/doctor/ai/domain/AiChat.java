package com.doctor.ai.domain;

import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import java.util.Date;

/**
 * AI聊天记录对象 ai_chat
 */
public class AiChat extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 聊天ID */
    private Long chatId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 助手类型 */
    @Excel(name = "助手类型")
    private Integer assistantType;

    /** 对话标题 */
    @Excel(name = "对话标题")
    private String title;

    /** 最后消息时间 */
    @Excel(name = "最后消息时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date lastMessageTime;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getAssistantType() {
        return assistantType;
    }

    public void setAssistantType(Integer assistantType) {
        this.assistantType = assistantType;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Date getLastMessageTime() {
        return lastMessageTime;
    }

    public void setLastMessageTime(Date lastMessageTime) {
        this.lastMessageTime = lastMessageTime;
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
                .append("chatId", getChatId())
                .append("userId", getUserId())
                .append("assistantType", getAssistantType())
                .append("title", getTitle())
                .append("lastMessageTime", getLastMessageTime())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
} 