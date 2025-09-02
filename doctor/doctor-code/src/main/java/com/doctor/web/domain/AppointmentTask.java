package com.doctor.web.domain;

import com.doctor.common.core.domain.BaseEntity;

import java.util.Date;

/**
 * 预约任务对象 appointment_task
 * 
 * @author Li
 */
public class AppointmentTask extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 任务ID */
    private Long taskId;

    /** 预约ID */
    private Long appointmentId;

    /** 患者ID */
    private Long patientId;

    /** 医生ID */
    private Long doctorId;
    
    /** 医生姓名 */
    private String doctorName;

    /** 计划执行时间 */
    private Date scheduledTime;

    /** 实际执行时间 */
    private Date executeTime;

    /** 任务状态（0待处理 1已处理 2处理失败） */
    private Integer status;

    /** 重试次数 */
    private Integer retryCount;

    /** 失败原因 */
    private String failReason;

    public Long getTaskId() {
        return taskId;
    }

    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }

    public Long getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(Long appointmentId) {
        this.appointmentId = appointmentId;
    }

    public Long getPatientId() {
        return patientId;
    }

    public void setPatientId(Long patientId) {
        this.patientId = patientId;
    }

    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }
    
    public String getDoctorName() {
        return doctorName;
    }
    
    public void setDoctorName(String doctorName) {
        this.doctorName = doctorName;
    }

    public Date getScheduledTime() {
        return scheduledTime;
    }

    public void setScheduledTime(Date scheduledTime) {
        this.scheduledTime = scheduledTime;
    }

    public Date getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Date executeTime) {
        this.executeTime = executeTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public String getFailReason() {
        return failReason;
    }

    public void setFailReason(String failReason) {
        this.failReason = failReason;
    }

    @Override
    public String toString() {
        return "AppointmentTask{" +
                "taskId=" + taskId +
                ", appointmentId=" + appointmentId +
                ", patientId=" + patientId +
                ", doctorId=" + doctorId +
                ", doctorName='" + doctorName + '\'' +
                ", scheduledTime=" + scheduledTime +
                ", executeTime=" + executeTime +
                ", status=" + status +
                ", retryCount=" + retryCount +
                ", failReason='" + failReason + '\'' +
                '}';
    }
} 