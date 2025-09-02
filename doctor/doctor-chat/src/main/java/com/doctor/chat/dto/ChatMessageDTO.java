package com.doctor.chat.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天消息传输对象
 */
@Data
public class ChatMessageDTO implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    private Long messageId;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 发送者名称
     */
    private String senderName;

    /**
     * 发送者头像
     */
    private String senderAvatar;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 接收者名称
     */
    private String receiverName;

    /**
     * 接收者头像
     */
    private String receiverAvatar;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 消息类型：0-文本，1-图片，2-语音，3-视频，4-文件
     */
    private Integer type;

    /**
     * 消息状态：0-未读，1-已读，2-已删除
     */
    private Integer status;

    /**
     * 发送时间
     */
    private Date createTime;

    /**
     * 额外数据（JSON格式，存储媒体文件URL等信息）
     */
    private String extraData;
    
    /**
     * 会话ID
     */
    private String conversationId;
} 