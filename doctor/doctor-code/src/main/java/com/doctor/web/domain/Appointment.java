package com.doctor.web.domain;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.doctor.common.annotation.Excel;
import com.doctor.common.core.domain.BaseEntity;

/**
 * 预约记录对象 t_appointment
 * 
 * @author Li
 * @date 2024-05-15
 */
@Data
public class Appointment extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** id */
    private Long appointmentId;

    /** 排班详情id */
    @Excel(name = "排班详情id")
    private Long doctorScheduledId;

    /** 患者用户id */
    @Excel(name = "患者用户id")
    private Long patientId;

    /** 医生用户id */
    @Excel(name = "医生用户id")
    private Long doctorId;

    /** 医生名称 */
    @Excel(name = "医生名称")
    private String doctorName;

    /** 真名 */
    @Excel(name = "真名")
    private String trueName;

    /** 联系号码 */
    @Excel(name = "联系号码")
    private String phone;

    /** 预约日期 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "预约日期", width = 30, dateFormat = "yyyy-MM-dd")
    private Date appointmentDate;

    /** 预约时段 */
    @Excel(name = "预约时段")
    private String appointmentTime;

    /** 预约码 */
    @Excel(name = "预约码")
    private String code;

    /** 预约状态(1预约，2取消，3就诊，4未就诊，5评论) */
    @Excel(name = "预约状态(1预约，2取消，3就诊，4未就诊，5评论)")
    private Integer appointmentStatus;

    /** 评分 */
    @Excel(name = "评分")
    private Integer score;

    /** 评论 */
    @Excel(name = "评论")
    private String comment;

    /** 评论时间 */
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Excel(name = "评论时间", width = 30, dateFormat = "yyyy-MM-dd")
    private Date commentTime;

    public void setAppointmentId(Long appointmentId) 
    {
        this.appointmentId = appointmentId;
    }

    public Long getAppointmentId() 
    {
        return appointmentId;
    }
    public void setDoctorScheduledId(Long doctorScheduledId) 
    {
        this.doctorScheduledId = doctorScheduledId;
    }

    public Long getDoctorScheduledId() 
    {
        return doctorScheduledId;
    }
    public void setPatientId(Long patientId) 
    {
        this.patientId = patientId;
    }

    public Long getPatientId() 
    {
        return patientId;
    }
    public void setDoctorId(Long doctorId) 
    {
        this.doctorId = doctorId;
    }

    public Long getDoctorId() 
    {
        return doctorId;
    }
    public void setDoctorName(String doctorName) 
    {
        this.doctorName = doctorName;
    }

    public String getDoctorName() 
    {
        return doctorName;
    }
    public void setTrueName(String trueName) 
    {
        this.trueName = trueName;
    }

    public String getTrueName() 
    {
        return trueName;
    }
    public void setPhone(String phone) 
    {
        this.phone = phone;
    }

    public String getPhone() 
    {
        return phone;
    }
    public void setAppointmentDate(Date appointmentDate) 
    {
        this.appointmentDate = appointmentDate;
    }

    public Date getAppointmentDate() 
    {
        return appointmentDate;
    }
    public void setAppointmentTime(String appointmentTime) 
    {
        this.appointmentTime = appointmentTime;
    }

    public String getAppointmentTime() 
    {
        return appointmentTime;
    }
    public void setAppointmentStatus(Integer appointmentStatus)
    {
        this.appointmentStatus = appointmentStatus;
    }

    public Integer getAppointmentStatus()
    {
        return appointmentStatus;
    }
    public void setScore(Integer score) 
    {
        this.score = score;
    }

    public Integer getScore() 
    {
        return score;
    }
    public void setComment(String comment) 
    {
        this.comment = comment;
    }

    public String getComment() 
    {
        return comment;
    }
    public void setCommentTime(Date commentTime) 
    {
        this.commentTime = commentTime;
    }

    public Date getCommentTime() 
    {
        return commentTime;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("appointmentId", getAppointmentId())
            .append("doctorScheduledId", getDoctorScheduledId())
            .append("patientId", getPatientId())
            .append("doctorId", getDoctorId())
            .append("doctorName", getDoctorName())
            .append("trueName", getTrueName())
            .append("phone", getPhone())
            .append("appointmentDate", getAppointmentDate())
            .append("appointmentTime", getAppointmentTime())
            .append("appointmentStatus", getAppointmentStatus())
            .append("score", getScore())
            .append("comment", getComment())
            .append("commentTime", getCommentTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
