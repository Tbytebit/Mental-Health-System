package com.doctor.web.mapper;

import java.util.List;
import com.doctor.web.domain.Scheduled;
import com.doctor.web.domain.vo.ScheduledVo;

/**
 * 预约排版Mapper接口
 * 
 * @author Li
 * @date 2024-05-13
 */
public interface ScheduledMapper 
{
    /**
     * 查询预约排版
     * 
     * @param scheduledId 预约排版主键
     * @return 预约排版
     */
    public Scheduled selectScheduledByScheduledId(Long scheduledId);

    /**
     * 查询预约排版列表
     * 
     * @param scheduled 预约排版
     * @return 预约排版集合
     */
    public List<ScheduledVo> selectScheduledList(ScheduledVo scheduled);

    /**
     * 新增预约排版
     * 
     * @param scheduled 预约排版
     * @return 结果
     */
    public int insertScheduled(Scheduled scheduled);

    /**
     * 修改预约排版
     * 
     * @param scheduled 预约排版
     * @return 结果
     */
    public int updateScheduled(Scheduled scheduled);

    /**
     * 删除预约排版
     * 
     * @param scheduledId 预约排版主键
     * @return 结果
     */
    public int deleteScheduledByScheduledId(Long scheduledId);

    /**
     * 批量删除预约排版
     * 
     * @param scheduledIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteScheduledByScheduledIds(Long[] scheduledIds);

    List<ScheduledVo> getCheduledList(ScheduledVo scheduled);
}
