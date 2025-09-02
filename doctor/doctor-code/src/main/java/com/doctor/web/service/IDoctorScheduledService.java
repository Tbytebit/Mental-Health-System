package com.doctor.web.service;

import java.util.List;
import com.doctor.web.domain.DoctorScheduled;
import com.doctor.web.domain.vo.DoctorScheduledVo;

/**
 * 医生预约排版Service接口
 * 
 * @author Li
 * @date 2024-05-13
 */
public interface IDoctorScheduledService 
{
    /**
     * 查询医生预约排版
     * 
     * @param id 医生预约排版主键
     * @return 医生预约排版
     */
    public DoctorScheduled selectDoctorScheduledById(Long id);

    /**
     * 查询医生预约排版列表
     * 
     * @param doctorScheduled 医生预约排版
     * @return 医生预约排版集合
     */
    public List<DoctorScheduledVo> selectDoctorScheduledList(DoctorScheduledVo doctorScheduled);

    /**
     * 新增医生预约排版
     * 
     * @param doctorScheduled 医生预约排版
     * @return 结果
     */
    public int insertDoctorScheduled(DoctorScheduled doctorScheduled);

    /**
     * 修改医生预约排版
     * 
     * @param doctorScheduled 医生预约排版
     * @return 结果
     */
    public int updateDoctorScheduled(DoctorScheduled doctorScheduled);

    /**
     * 批量删除医生预约排版
     * 
     * @param ids 需要删除的医生预约排版主键集合
     * @return 结果
     */
    public int deleteDoctorScheduledByIds(Long[] ids);

    /**
     * 删除医生预约排版信息
     * 
     * @param id 医生预约排版主键
     * @return 结果
     */
    public int deleteDoctorScheduledById(Long id);

    List<DoctorScheduledVo> getDoctorScheduledList(DoctorScheduledVo doctorScheduled);
}
