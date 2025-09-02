package com.doctor.web.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;

/**
 * 广告对象 t_advertisements
 * 
 * @author li
 * @date 2024-04-10
 */
@Data
public class Advertisements extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 广告ID */
    private Long adId;

    /** 广告标题 */
    @Excel(name = "广告标题")
    private String title;

    /** 广告内容 */
    @Excel(name = "广告内容")
    private String content;

    /** 广告链接 */
    @Excel(name = "广告链接")
    private String link;

    /** 上线时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "上线时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 过期时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "过期时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date expirationTime;

    /** 点击次数 */
    @Excel(name = "点击次数")
    private Long clicks;

    /** 广告状态 */
    @Excel(name = "广告状态")
    private Integer status;

    /** 广告图片地址 */
    @Excel(name = "广告图片地址")
    private String imageUrl;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;

}
