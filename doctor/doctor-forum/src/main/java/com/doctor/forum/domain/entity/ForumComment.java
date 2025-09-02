package com.doctor.forum.domain.entity;

import lombok.Data;
import com.doctor.common.core.domain.BaseEntity;

import java.util.List;

/**
 * 论坛评论实体类
 */
@Data
public class ForumComment extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /** 评论ID */
    private Long id;
    
    /** 帖子ID */
    private Long postId;
    
    /** 评论内容 */
    private String content;
    
    /** 用户ID */
    private Long userId;
    
    /** 用户名 */
    private String username;
    
    /** 用户头像 */
    private String avatar;
    
    /** 父评论ID，如果是一级评论则为0 */
    private Long parentId;
    
    /** 回复评论ID */
    private Long replyId;
    
    /** 回复用户ID */
    private Long replyUserId;
    
    /** 回复用户名 */
    private String replyUsername;
    
    /** 点赞数量 */
    private Integer likeCount;
    
    /** 是否匿名评论 */
    private Boolean isAnonymous;
    
    /** 删除标志（0代表存在 1代表删除） */
    private String delFlag;
    
    /** 是否已点赞，非数据库字段 */
    private Boolean isLiked;
    
    /** 子评论列表，非数据库字段 */
    private List<ForumComment> children;
} 