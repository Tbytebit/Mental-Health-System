package com.doctor.web.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;

/**
 * 预约排版对象 t_scheduled
 * 
 * @author Li
 * @date 2024-05-13
 */
@Data
public class Scheduled extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 排班id */

    private Long scheduledId;

    /** 医生id */
    private Long doctorId;

    /** 排班日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "排班日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date time;

    /** 状态 */
    @Excel(name = "状态")
    private Long status;

    public void setScheduledId(Long scheduledId) 
    {
        this.scheduledId = scheduledId;
    }

    public Long getScheduledId() 
    {
        return scheduledId;
    }
    public void setDoctorId(Long doctorId) 
    {
        this.doctorId = doctorId;
    }

    public Long getDoctorId() 
    {
        return doctorId;
    }
    public void setTime(Date time) 
    {
        this.time = time;
    }

    public Date getTime() 
    {
        return time;
    }
    public void setStatus(Long status) 
    {
        this.status = status;
    }

    public Long getStatus() 
    {
        return status;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("scheduledId", getScheduledId())
            .append("doctorId", getDoctorId())
            .append("time", getTime())
            .append("status", getStatus())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
