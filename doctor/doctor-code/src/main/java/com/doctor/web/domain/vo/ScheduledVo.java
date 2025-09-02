package com.doctor.web.domain.vo;


import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 预约排版对象 t_scheduled
 * 
 * @author Li
 * @date 2024-05-13
 */
@Data
public class ScheduledVo extends BaseEntity {

    /** 排班id */
    private Long scheduledId;

    /** 医生id */
    private Long doctorId;


    /** 部门id */
    private Long deptId;

    /** 医生 */
    @Excel(name = "医生")
    private String doctorName;

    /** 医生图片 */
    @Excel(name = "医生图片")
    private String avatar;

    /** 排班日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "排班日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date time;

    /** 状态 */
    @Excel(name = "状态")
    private Long status;





}
