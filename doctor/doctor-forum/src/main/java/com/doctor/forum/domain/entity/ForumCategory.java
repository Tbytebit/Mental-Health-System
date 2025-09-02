package com.doctor.forum.domain.entity;

import lombok.Data;
import com.doctor.common.core.domain.BaseEntity;

/**
 * 论坛分类实体类
 */
@Data
public class ForumCategory extends BaseEntity {
    
    private static final long serialVersionUID = 1L;

    /** 分类ID */
    private String id;
    
    /** 分类名称 */
    private String name;
    
    /** 分类描述 */
    private String description;
    
    /** 排序号 */
    private Integer orderNum;
    
    /** 删除标志（0代表存在 1代表删除） */
    private String delFlag;
} 