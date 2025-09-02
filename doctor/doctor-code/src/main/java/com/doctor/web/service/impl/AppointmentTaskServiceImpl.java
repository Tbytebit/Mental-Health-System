package com.doctor.web.service.impl;

import com.doctor.common.utils.DateUtils;
import com.doctor.web.domain.AppointmentTask;
import com.doctor.web.mapper.AppointmentTaskMapper;
import com.doctor.web.service.IAppointmentTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * 预约任务Service业务层处理
 * 
 * @author Li
 */
@Service
public class AppointmentTaskServiceImpl implements IAppointmentTaskService {
    @Autowired
    private AppointmentTaskMapper appointmentTaskMapper;

    /**
     * 查询预约任务
     * 
     * @param taskId 预约任务ID
     * @return 预约任务
     */
    @Override
    public AppointmentTask selectAppointmentTaskById(Long taskId) {
        return appointmentTaskMapper.selectAppointmentTaskById(taskId);
    }

    /**
     * 查询预约任务列表
     * 
     * @param appointmentTask 预约任务
     * @return 预约任务
     */
    @Override
    public List<AppointmentTask> selectAppointmentTaskList(AppointmentTask appointmentTask) {
        return appointmentTaskMapper.selectAppointmentTaskList(appointmentTask);
    }

    /**
     * 查询待处理的预约任务
     * 
     * @param currentTime 当前时间
     * @return 预约任务集合
     */
    @Override
    public List<AppointmentTask> findPendingTasks(Date currentTime) {
        return appointmentTaskMapper.selectPendingTasks(currentTime);
    }

    /**
     * 新增预约任务
     * 
     * @param appointmentTask 预约任务
     * @return 结果
     */
    @Override
    public int insertAppointmentTask(AppointmentTask appointmentTask) {
        appointmentTask.setCreateTime(DateUtils.getNowDate());
        return appointmentTaskMapper.insertAppointmentTask(appointmentTask);
    }

    /**
     * 修改预约任务
     * 
     * @param appointmentTask 预约任务
     * @return 结果
     */
    @Override
    public int updateAppointmentTask(AppointmentTask appointmentTask) {
        appointmentTask.setUpdateTime(DateUtils.getNowDate());
        return appointmentTaskMapper.updateAppointmentTask(appointmentTask);
    }

    /**
     * 批量删除预约任务
     * 
     * @param taskIds 需要删除的预约任务ID
     * @return 结果
     */
    @Override
    public int deleteAppointmentTaskByIds(Long[] taskIds) {
        return appointmentTaskMapper.deleteAppointmentTaskByIds(taskIds);
    }

    /**
     * 删除预约任务信息
     * 
     * @param taskId 预约任务ID
     * @return 结果
     */
    @Override
    public int deleteAppointmentTaskById(Long taskId) {
        return appointmentTaskMapper.deleteAppointmentTaskById(taskId);
    }
} 