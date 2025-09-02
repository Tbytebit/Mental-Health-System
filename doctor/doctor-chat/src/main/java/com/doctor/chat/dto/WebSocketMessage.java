package com.doctor.chat.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * WebSocket消息对象
 */
@Data
public class WebSocketMessage implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 消息类型：
     * 1-建立连接
     * 2-心跳消息
     * 3-聊天消息
     * 4-已读回执
     * 5-好友请求
     * 6-好友请求响应
     * 7-离线消息
     */
    private Integer type;

    /**
     * 消息内容（JSON字符串）
     */
    private String data;
} 