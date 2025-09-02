package com.doctor.forum.domain.entity;

import lombok.Data;
import com.doctor.common.core.domain.BaseEntity;

/**
 * 论坛点赞记录实体类
 */
@Data
public class ForumLike extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /** 记录ID */
    private Long id;
    
    /** 点赞类型：1-帖子，2-评论 */
    private Integer type;
    
    /** 帖子/评论ID */
    private Long targetId;
    
    /** 用户ID */
    private Long userId;
    
    /** 删除标志（0代表存在 1代表删除） */
    private String delFlag;
} 