package com.doctor.ai.domain;

import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * AI消息对象 ai_message
 */
public class AiMessage extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 消息ID */
    private Long messageId;

    /** 聊天ID */
    @Excel(name = "聊天ID")
    private Long chatId;

    /** 用户ID */
    @Excel(name = "用户ID")
    private Long userId;

    /** 内容 */
    @Excel(name = "内容")
    private String content;

    /** 是否用户发送（0否 1是） */
    @Excel(name = "是否用户发送", readConverterExp = "0=否,1=是")
    private String isUser;

    /** 消息类型（text文本 image图片） */
    @Excel(name = "消息类型", readConverterExp = "t=ext文本,i=mage图片")
    private String messageType;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public Long getMessageId() {
        return messageId;
    }

    public void setMessageId(Long messageId) {
        this.messageId = messageId;
    }

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getIsUser() {
        return isUser;
    }

    public void setIsUser(String isUser) {
        this.isUser = isUser;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
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
                .append("messageId", getMessageId())
                .append("chatId", getChatId())
                .append("userId", getUserId())
                .append("content", getContent())
                .append("isUser", getIsUser())
                .append("messageType", getMessageType())
                .append("delFlag", getDelFlag())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
} 