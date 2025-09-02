package com.doctor.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 聊天记录清空记录实体类
 */
@Data
@TableName("chat_message_clear")
public class ChatMessageClear implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 记录ID
     */
    @TableId(value = "clear_id", type = IdType.AUTO)
    private Long clearId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 好友ID
     */
    private Long friendId;
    
    /**
     * 会话ID
     */
    private String conversationId;

    /**
     * 清空时间
     */
    private Date clearTime;
} 