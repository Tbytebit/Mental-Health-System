package com.doctor.forum.domain.entity;

import org.springframework.beans.BeanUtils;
import lombok.Data;
import com.doctor.common.core.domain.BaseEntity;

import java.util.Date;
import java.util.List;

/**
 * 论坛帖子实体类
 */
@Data
public class ForumPost extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

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
    
    /** 图片列表，JSON字符串存储 */
    private String images;
    
    /** 是否匿名发布 */
    private Boolean isAnonymous;
    
    /** 点赞数量 */
    private Integer likeCount;
    
    /** 评论数量 */
    private Integer commentCount;
    
    /** 浏览数量 */
    private Integer viewCount;
    
    /** 删除标志（0代表存在 1代表删除） */
    private String delFlag;
    
    /** 图片数组，非数据库字段 */
    private List<String> imageList;
    
    /** 是否已点赞，非数据库字段 */
    private Boolean isLiked;
} 