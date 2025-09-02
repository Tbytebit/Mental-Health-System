package com.doctor.web.service.impl;

import java.util.List;
import com.doctor.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doctor.web.mapper.AppointmentMapper;
import com.doctor.web.domain.Appointment;
import com.doctor.web.service.IAppointmentService;

/**
 * 预约记录Service业务层处理
 * 
 * @author Li
 * @date 2024-05-15
 */
@Service
public class AppointmentServiceImpl implements IAppointmentService 
{
    @Autowired
    private AppointmentMapper appointmentMapper;

    /**
     * 查询预约记录
     * 
     * @param appointmentId 预约记录主键
     * @return 预约记录
     */
    @Override
    public Appointment selectAppointmentByAppointmentId(Long appointmentId)
    {
        return appointmentMapper.selectAppointmentByAppointmentId(appointmentId);
    }

    /**
     * 查询预约记录列表
     * 
     * @param appointment 预约记录
     * @return 预约记录
     */
    @Override
    public List<Appointment> selectAppointmentList(Appointment appointment)
    {
        return appointmentMapper.selectAppointmentList(appointment);
    }

    /**
     * 新增预约记录
     * 
     * @param appointment 预约记录
     * @return 结果
     */
    @Override
    public int insertAppointment(Appointment appointment)
    {
        appointment.setCreateTime(DateUtils.getNowDate());
        return appointmentMapper.insertAppointment(appointment);
    }

    /**
     * 修改预约记录
     * 
     * @param appointment 预约记录
     * @return 结果
     */
    @Override
    public int updateAppointment(Appointment appointment)
    {
        appointment.setUpdateTime(DateUtils.getNowDate());
        return appointmentMapper.updateAppointment(appointment);
    }

    /**
     * 批量删除预约记录
     * 
     * @param appointmentIds 需要删除的预约记录主键
     * @return 结果
     */
    @Override
    public int deleteAppointmentByAppointmentIds(Long[] appointmentIds)
    {
        return appointmentMapper.deleteAppointmentByAppointmentIds(appointmentIds);
    }

    /**
     * 删除预约记录信息
     * 
     * @param appointmentId 预约记录主键
     * @return 结果
     */
    @Override
    public int deleteAppointmentByAppointmentId(Long appointmentId)
    {
        return appointmentMapper.deleteAppointmentByAppointmentId(appointmentId);
    }
    /**
     * 用户查询自己的预约记录
     */
    @Override
    public List<Appointment> getUserCommentAppointment(Appointment appointment) {
        return appointmentMapper.getUserCommentAppointment(appointment);
    }
}
