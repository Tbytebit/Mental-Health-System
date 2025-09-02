package com.doctor.diary.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 用户心理日记实体类
 */
@Data
public class UserDiary {
    
    /**
     * 日记ID
     */
    private Long id;
    
    /**
     * 用户ID
     */
    private Long userId;
    
    /**
     * 日记标题
     */
    private String diaryName;
    
    /**
     * 日记内容
     */
    private String diaryContent;
    
    /**
     * 心情状态 (happy, neutral, sad, angry, anxious)
     */
    private String mood;
    
    /**
     * 图片路径，JSON格式存储
     */
    private String images;
    
    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
    
    /**
     *, 首页更新时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date updateTime;
    
    /**
     * 备注
     */
    private String remark;
    
    /**
     * 删除标志（0代表存在 1代表删除）
     */
    private String delFlag;
} 