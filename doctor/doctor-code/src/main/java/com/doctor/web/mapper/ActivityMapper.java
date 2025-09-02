package com.doctor.web.mapper;

import java.util.List;
import com.doctor.web.domain.Activity;

/**
 * 活动管理Mapper接口
 * 
 * @author ruoyi
 * @date 2024-05-24
 */
public interface ActivityMapper 
{
    /**
     * 查询活动管理
     * 
     * @param id 活动管理主键
     * @return 活动管理
     */
    public Activity selectActivityById(Long id);

    /**
     * 查询活动管理列表
     * 
     * @param activity 活动管理
     * @return 活动管理集合
     */
    public List<Activity> selectActivityList(Activity activity);

    /**
     * 新增活动管理
     * 
     * @param activity 活动管理
     * @return 结果
     */
    public int insertActivity(Activity activity);

    /**
     * 修改活动管理
     * 
     * @param activity 活动管理
     * @return 结果
     */
    public int updateActivity(Activity activity);

    /**
     * 删除活动管理
     * 
     * @param id 活动管理主键
     * @return 结果
     */
    public int deleteActivityById(Long id);

    /**
     * 批量删除活动管理
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteActivityByIds(Long[] ids);
}
