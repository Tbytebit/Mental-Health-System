package com.doctor.forum.domain.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 论坛评论VO对象
 */
@Data
public class ForumCommentVO {
    
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
    
    /** 父评论ID */
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
    
    /** 是否已点赞 */
    private Boolean isLiked;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    
    /** 子评论列表 */
    private List<ForumCommentVO> children;
} 