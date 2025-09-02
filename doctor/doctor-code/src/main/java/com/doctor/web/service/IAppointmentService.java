package com.doctor.web.service;

import java.util.List;
import com.doctor.web.domain.Appointment;

/**
 * 预约记录Service接口
 * 
 * @author Li
 * @date 2024-05-15
 */
public interface IAppointmentService 
{
    /**
     * 查询预约记录
     * 
     * @param appointmentId 预约记录主键
     * @return 预约记录
     */
    public Appointment selectAppointmentByAppointmentId(Long appointmentId);

    /**
     * 查询预约记录列表
     * 
     * @param appointment 预约记录
     * @return 预约记录集合
     */
    public List<Appointment> selectAppointmentList(Appointment appointment);

    /**
     * 新增预约记录
     * 
     * @param appointment 预约记录
     * @return 结果
     */
    public int insertAppointment(Appointment appointment);

    /**
     * 修改预约记录
     * 
     * @param appointment 预约记录
     * @return 结果
     */
    public int updateAppointment(Appointment appointment);

    /**
     * 批量删除预约记录
     * 
     * @param appointmentIds 需要删除的预约记录主键集合
     * @return 结果
     */
    public int deleteAppointmentByAppointmentIds(Long[] appointmentIds);

    /**
     * 删除预约记录信息
     * 
     * @param appointmentId 预约记录主键
     * @return 结果
     */
    public int deleteAppointmentByAppointmentId(Long appointmentId);
    /**
     * 用户查询自己的预约记录
     */
    List<Appointment> getUserCommentAppointment(Appointment appointment);
}
