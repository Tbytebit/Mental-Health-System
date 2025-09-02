package com.doctor.forum.domain.vo;

import lombok.Data;
import java.util.Date;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 论坛帖子VO对象
 */
@Data
public class ForumPostVO {
    
    /** 帖子ID */
    private Long id;
    
    /** 帖子标题 */
    private String title;
    
    /** 帖子内容 */
    private String content;
    
    /** 用户ID */
    private Long userId;
    
    /** 用户名 */
    private String username;
    
    /** 用户头像 */
    private String avatar;
    
    /** 分类ID */
    private String categoryId;
    
    /** 分类名称 */
    private String categoryName;
    
    /** 图片列表 */
    private List<String> images;
    
    /** 是否匿名发布 */
    private Boolean isAnonymous;
    
    /** 点赞数量 */
    private Integer likeCount;
    
    /** 评论数量 */
    private Integer commentCount;
    
    /** 浏览数量 */
    private Integer viewCount;
    
    /** 是否已点赞 */
    private Boolean isLiked;
    
    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
} 