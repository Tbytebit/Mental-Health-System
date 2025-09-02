package com.doctor.web.service.impl;

import java.util.List;
import com.doctor.common.utils.DateUtils;
import com.doctor.web.domain.vo.DoctorScheduledVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doctor.web.mapper.DoctorScheduledMapper;
import com.doctor.web.domain.DoctorScheduled;
import com.doctor.web.service.IDoctorScheduledService;

/**
 * 医生预约排版Service业务层处理
 * 
 * @author Li
 * @date 2024-05-13
 */
@Service
public class DoctorScheduledServiceImpl implements IDoctorScheduledService 
{
    @Autowired
    private DoctorScheduledMapper doctorScheduledMapper;

    /**
     * 查询医生预约排版
     * 
     * @param id 医生预约排版主键
     * @return 医生预约排版
     */
    @Override
    public DoctorScheduled selectDoctorScheduledById(Long id)
    {
        return doctorScheduledMapper.selectDoctorScheduledById(id);
    }

    /**
     * 查询医生预约排版列表
     * 
     * @param doctorScheduled 医生预约排版
     * @return 医生预约排版
     */
    @Override
    public List<DoctorScheduledVo> selectDoctorScheduledList(DoctorScheduledVo doctorScheduled)
    {
        return doctorScheduledMapper.selectDoctorScheduledList(doctorScheduled);
    }

    /**
     * 新增医生预约排版
     * 
     * @param doctorScheduled 医生预约排版
     * @return 结果
     */
    @Override
    public int insertDoctorScheduled(DoctorScheduled doctorScheduled)
    {
        doctorScheduled.setCreateTime(DateUtils.getNowDate());
        return doctorScheduledMapper.insertDoctorScheduled(doctorScheduled);
    }

    /**
     * 修改医生预约排版
     * 
     * @param doctorScheduled 医生预约排版
     * @return 结果
     */
    @Override
    public int updateDoctorScheduled(DoctorScheduled doctorScheduled)
    {
        doctorScheduled.setUpdateTime(DateUtils.getNowDate());
        return doctorScheduledMapper.updateDoctorScheduled(doctorScheduled);
    }

    /**
     * 批量删除医生预约排版
     * 
     * @param ids 需要删除的医生预约排版主键
     * @return 结果
     */
    @Override
    public int deleteDoctorScheduledByIds(Long[] ids)
    {
        return doctorScheduledMapper.deleteDoctorScheduledByIds(ids);
    }

    /**
     * 删除医生预约排版信息
     * 
     * @param id 医生预约排版主键
     * @return 结果
     */
    @Override
    public int deleteDoctorScheduledById(Long id)
    {
        return doctorScheduledMapper.deleteDoctorScheduledById(id);
    }

    @Override
    public List<DoctorScheduledVo> getDoctorScheduledList(DoctorScheduledVo doctorScheduled) {
        return doctorScheduledMapper.getDoctorScheduledList(doctorScheduled);
    }
}
