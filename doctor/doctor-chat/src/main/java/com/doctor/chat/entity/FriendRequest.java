package com.doctor.chat.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 好友申请实体类
 */
@Data
@TableName("chat_friend_request")
public class FriendRequest implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 请求ID
     */
    @TableId(value = "request_id", type = IdType.AUTO)
    private Long requestId;

    /**
     * 申请人ID
     */
    private Long fromUserId;

    /**
     * 接收人ID
     */
    private Long toUserId;

    /**
     * 申请消息
     */
    private String message;
    
    /**
     * 好友备注
     */
    private String remark;

    /**
     * 状态：0-待处理，1-已接受，2-已拒绝
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
} 