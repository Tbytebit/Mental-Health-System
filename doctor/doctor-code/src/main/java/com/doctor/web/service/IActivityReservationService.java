package com.doctor.web.service;

import java.util.List;
import com.doctor.web.domain.ActivityReservation;

/**
 * 活动预约Service接口
 * 
 * @author ruoyi
 * @date 2024-05-24
 */
public interface IActivityReservationService 
{
    /**
     * 查询活动预约
     * 
     * @param id 活动预约主键
     * @return 活动预约
     */
    public ActivityReservation selectActivityReservationById(Long id);

    /**
     * 查询活动预约列表
     * 
     * @param activityReservation 活动预约
     * @return 活动预约集合
     */
    public List<ActivityReservation> selectActivityReservationList(ActivityReservation activityReservation);

    /**
     * 新增活动预约
     * 
     * @param activityReservation 活动预约
     * @return 结果
     */
    public int insertActivityReservation(ActivityReservation activityReservation);

    /**
     * 修改活动预约
     * 
     * @param activityReservation 活动预约
     * @return 结果
     */
    public int updateActivityReservation(ActivityReservation activityReservation);

    /**
     * 批量删除活动预约
     * 
     * @param ids 需要删除的活动预约主键集合
     * @return 结果
     */
    public int deleteActivityReservationByIds(Long[] ids);

    /**
     * 删除活动预约信息
     * 
     * @param id 活动预约主键
     * @return 结果
     */
    public int deleteActivityReservationById(Long id);
}
