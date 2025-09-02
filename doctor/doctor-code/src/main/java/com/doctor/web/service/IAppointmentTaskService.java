package com.doctor.web.service;

import com.doctor.web.domain.AppointmentTask;

import java.util.Date;
import java.util.List;

/**
 * 预约任务服务接口
 * 
 * @author Li
 */
public interface IAppointmentTaskService {
    /**
     * 查询预约任务
     * 
     * @param taskId 预约任务ID
     * @return 预约任务
     */
    public AppointmentTask selectAppointmentTaskById(Long taskId);

    /**
     * 查询预约任务列表
     * 
     * @param appointmentTask 预约任务
     * @return 预约任务集合
     */
    public List<AppointmentTask> selectAppointmentTaskList(AppointmentTask appointmentTask);

    /**
     * 查询待处理的预约任务
     * 
     * @param currentTime 当前时间
     * @return 预约任务集合
     */
    public List<AppointmentTask> findPendingTasks(Date currentTime);

    /**
     * 新增预约任务
     * 
     * @param appointmentTask 预约任务
     * @return 结果
     */
    public int insertAppointmentTask(AppointmentTask appointmentTask);

    /**
     * 修改预约任务
     * 
     * @param appointmentTask 预约任务
     * @return 结果
     */
    public int updateAppointmentTask(AppointmentTask appointmentTask);

    /**
     * 批量删除预约任务
     * 
     * @param taskIds 需要删除的预约任务ID
     * @return 结果
     */
    public int deleteAppointmentTaskByIds(Long[] taskIds);

    /**
     * 删除预约任务信息
     * 
     * @param taskId 预约任务ID
     * @return 结果
     */
    public int deleteAppointmentTaskById(Long taskId);
} 