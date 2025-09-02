package com.doctor.web.service.impl;

import java.util.List;
import com.doctor.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doctor.web.mapper.ActivityReservationMapper;
import com.doctor.web.domain.ActivityReservation;
import com.doctor.web.service.IActivityReservationService;

/**
 * 活动预约Service业务层处理
 * 
 * @author ruoyi
 * @date 2024-05-24
 */
@Service
public class ActivityReservationServiceImpl implements IActivityReservationService 
{
    @Autowired
    private ActivityReservationMapper activityReservationMapper;

    /**
     * 查询活动预约
     * 
     * @param id 活动预约主键
     * @return 活动预约
     */
    @Override
    public ActivityReservation selectActivityReservationById(Long id)
    {
        return activityReservationMapper.selectActivityReservationById(id);
    }

    /**
     * 查询活动预约列表
     * 
     * @param activityReservation 活动预约
     * @return 活动预约
     */
    @Override
    public List<ActivityReservation> selectActivityReservationList(ActivityReservation activityReservation)
    {
        return activityReservationMapper.selectActivityReservationList(activityReservation);
    }

    /**
     * 新增活动预约
     * 
     * @param activityReservation 活动预约
     * @return 结果
     */
    @Override
    public int insertActivityReservation(ActivityReservation activityReservation)
    {
        activityReservation.setCreateTime(DateUtils.getNowDate());
        return activityReservationMapper.insertActivityReservation(activityReservation);
    }

    /**
     * 修改活动预约
     * 
     * @param activityReservation 活动预约
     * @return 结果
     */
    @Override
    public int updateActivityReservation(ActivityReservation activityReservation)
    {
        activityReservation.setUpdateTime(DateUtils.getNowDate());
        return activityReservationMapper.updateActivityReservation(activityReservation);
    }

    /**
     * 批量删除活动预约
     * 
     * @param ids 需要删除的活动预约主键
     * @return 结果
     */
    @Override
    public int deleteActivityReservationByIds(Long[] ids)
    {
        return activityReservationMapper.deleteActivityReservationByIds(ids);
    }

    /**
     * 删除活动预约信息
     * 
     * @param id 活动预约主键
     * @return 结果
     */
    @Override
    public int deleteActivityReservationById(Long id)
    {
        return activityReservationMapper.deleteActivityReservationById(id);
    }
}
