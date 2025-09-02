package com.doctor.web.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;

/**
 * 用户反馈对象 t_feedback
 * 
 * @author LI
 * @date 2024-05-09
 */
@Data
public class Feedback extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 反馈ID */
    private Long feedbackId;

    /** 用户ID */
    private Long userId;

    /** 反馈内容 */
    @Excel(name = "反馈内容")
    private String feedbackContent;

    /** 反馈类型 */
    @Excel(name = "反馈类型")
    private String feedbackType;

    /** 反馈时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "反馈时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date feedbackTime;

    /** 联系方式 */
    @Excel(name = "联系方式")
    private String phone;

    /** 回复 */
    @Excel(name = "回复")
    private String feedbackReply;

    /** 图片 */
    @Excel(name = "图片")
    private String url;

    /** 删除标志（0代表存在 2代表删除） */
    private String delFlag;


}
