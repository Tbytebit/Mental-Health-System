package com.doctor.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天消息实体类
 */
@Data
@TableName("chat_message")
public class ChatMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @TableId(value = "message_id", type = IdType.AUTO)
    private Long messageId;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 接收者ID
     */
    private Long receiverId;

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
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 额外数据（JSON格式，存储媒体文件URL等信息）
     */
    private String extraData;
    
    /**
     * 会话ID
     */
    private String conversationId;
} 