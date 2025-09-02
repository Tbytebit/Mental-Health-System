package com.doctor.forum.domain.dto;

import lombok.Data;
import java.util.List;

/**
 * 论坛帖子DTO对象
 */
@Data
public class ForumPostDTO {
    
    /** 帖子ID */
    private Long id;
    
    /** 帖子标题 */
    private String title;
    
    /** 帖子内容 */
    private String content;
    
    /** 用户ID */
    private Long userId;
    
    /** 分类ID */
    private String categoryId;
    
    /** 图片列表 */
    private List<String> images;
    
    /** 是否匿名发布 */
    private Boolean isAnonymous;
} 