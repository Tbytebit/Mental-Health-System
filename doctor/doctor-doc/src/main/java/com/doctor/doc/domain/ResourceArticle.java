package com.doctor.doc.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 心理资料文章实体
 */
@Data
@TableName("doc_resource_article")
public class ResourceArticle implements Serializable {
    
    private static final long serialVersionUID = 1L;

    /**
     * 文章ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 分类ID
     */
    private Long categoryId;
    
    /**
     * 文章标题
     */
    private String title;
    
    /**
     * 文章描述
     */
    private String description;
    
    /**
     * 封面图片
     */
    private String coverImage;
    
    /**
     * 文章内容
     */
    private String content;
    
    /**
     * 是否推荐(0:否 1:是)
     */
    private Integer isFeatured;
    
    /**
     * 阅读量
     */
    private Integer viewCount;
    
    /**
     * 创建者
     */
    private String createBy;
    
    /**
     * 创建时间
     */
    private Date createTime;
    
    /**
     * 更新者
     */
    private String updateBy;
    
    /**
     * 更新时间
     */
    private Date updateTime;
    
    /**
     * 备注
     */
    private String remark;
} 