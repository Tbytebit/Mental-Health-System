package com.doctor.forum.domain.dto;

import lombok.Data;

/**
 * 论坛评论DTO对象
 */
@Data
public class ForumCommentDTO {
    
    /** 评论ID */
    private Long id;
    
    /** 帖子ID */
    private Long postId;
    
    /** 评论内容 */
    private String content;
    
    /** 用户ID */
    private Long userId;
    
    /** 父评论ID */
    private Long parentId;
    
    /** 回复评论ID */
    private Long replyId;
    
    /** 回复用户ID */
    private Long replyUserId;
    
    /** 是否匿名评论 */
    private Boolean isAnonymous;
} 