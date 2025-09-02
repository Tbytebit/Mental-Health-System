package com.doctor.web.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;

/**
 * 医生预约排版对象 t_doctor_scheduled
 * 
 * @author Li
 * @date 2024-05-13
 */
@Data
public class DoctorScheduled extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long id;

    /** 排班id */
    @Excel(name = "排班id")
    private Long scheduledId;

    /** 医生id */
    @Excel(name = "医生id")
    private Long doctorId;

    /** 排班时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "排班时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date time;

    /** 开始时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "开始时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date startTime;

    /** 结束时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Excel(name = "结束时间", width = 30, dateFormat = "yyyy-MM-dd HH:mm:ss")
    private Date endTime;

    /** 可预约数 */
    @Excel(name = "可预约数")
    private Integer number;

    /** 剩余号数 */
    @Excel(name = "剩余号数")
    private Integer remainder;

    /** 状态 */
    @Excel(name = "状态")
    private Long status;
}
