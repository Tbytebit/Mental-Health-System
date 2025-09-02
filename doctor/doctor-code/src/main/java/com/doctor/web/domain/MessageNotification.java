package com.doctor.web.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;

/**
 * 消息通知对象 t_message_notification
 * 
 * @author LI
 * @date 2024-05-09
 */
public class MessageNotification extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 消息ID */
    private Long messageId;

    /** 发送者ID */
    @Excel(name = "发送者ID")
    private Long senderUserId;

    /** 接收者ID */
    @Excel(name = "接收者ID")
    private Long recipientUserId;

    /** 消息标题 */
    @Excel(name = "消息标题")
    private String messageName;

    /** 消息类型 */
    @Excel(name = "消息类型")
    private Integer messageType;

    /** 消息内容 */
    @Excel(name = "消息内容")
    private String messageContent;

    /** 发送时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "发送时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date sendTime;

    /** 是否已读 */
    @Excel(name = "是否已读")
    private Integer isRead;

    /** 附件 */
    @Excel(name = "附件")
    private String attachments;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

    public void setMessageId(Long messageId) 
    {
        this.messageId = messageId;
    }

    public Long getMessageId() 
    {
        return messageId;
    }
    public void setSenderUserId(Long senderUserId) 
    {
        this.senderUserId = senderUserId;
    }

    public Long getSenderUserId() 
    {
        return senderUserId;
    }
    public void setRecipientUserId(Long recipientUserId) 
    {
        this.recipientUserId = recipientUserId;
    }

    public Long getRecipientUserId() 
    {
        return recipientUserId;
    }
    public void setMessageName(String messageName) 
    {
        this.messageName = messageName;
    }

    public String getMessageName() 
    {
        return messageName;
    }
    public void setMessageType(Integer messageType) 
    {
        this.messageType = messageType;
    }

    public Integer getMessageType() 
    {
        return messageType;
    }
    public void setMessageContent(String messageContent) 
    {
        this.messageContent = messageContent;
    }

    public String getMessageContent() 
    {
        return messageContent;
    }
    public void setSendTime(Date sendTime) 
    {
        this.sendTime = sendTime;
    }

    public Date getSendTime() 
    {
        return sendTime;
    }
    public void setIsRead(Integer isRead) 
    {
        this.isRead = isRead;
    }

    public Integer getIsRead() 
    {
        return isRead;
    }
    public void setAttachments(String attachments) 
    {
        this.attachments = attachments;
    }

    public String getAttachments() 
    {
        return attachments;
    }
    public void setDelFlag(String delFlag) 
    {
        this.delFlag = delFlag;
    }

    public String getDelFlag() 
    {
        return delFlag;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("messageId", getMessageId())
            .append("senderUserId", getSenderUserId())
            .append("recipientUserId", getRecipientUserId())
            .append("messageName", getMessageName())
            .append("messageType", getMessageType())
            .append("messageContent", getMessageContent())
            .append("sendTime", getSendTime())
            .append("isRead", getIsRead())
            .append("attachments", getAttachments())
            .append("delFlag", getDelFlag())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
