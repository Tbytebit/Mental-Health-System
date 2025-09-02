package com.doctor.doc.domain.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 心理资料文章传输对象
 */
@Data
public class ResourceArticleDto implements Serializable {
    
    private static final long serialVersionUID = 1L;

    private Long id;
    
    private Long categoryId;
    
    private String categoryName;
    
    private String title;
    
    private String description;
    
    private String coverImage;
    
    private String content;
    
    private Integer isFeatured;
    
    private Integer viewCount;
    
    private Date createTime;
    
    private String remark;
} 