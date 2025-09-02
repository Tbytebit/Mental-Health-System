package com.doctor.web.service.impl;

import java.util.List;
import com.doctor.common.utils.DateUtils;
import com.doctor.web.domain.vo.ScheduledVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doctor.web.mapper.ScheduledMapper;
import com.doctor.web.domain.Scheduled;
import com.doctor.web.service.IScheduledService;

/**
 * 预约排版Service业务层处理
 * 
 * @author Li
 * @date 2024-05-13
 */
@Service
public class ScheduledServiceImpl implements IScheduledService 
{
    @Autowired
    private ScheduledMapper scheduledMapper;

    /**
     * 查询预约排版
     * 
     * @param scheduledId 预约排版主键
     * @return 预约排版
     */
    @Override
    public Scheduled selectScheduledByScheduledId(Long scheduledId)
    {
        return scheduledMapper.selectScheduledByScheduledId(scheduledId);
    }

    /**
     * 查询预约排版列表
     * 
     * @param scheduled 预约排版
     * @return 预约排版
     */
    @Override
    public List<ScheduledVo> selectScheduledList(ScheduledVo scheduled)
    {
        return scheduledMapper.selectScheduledList(scheduled);
    }

    /**
     * 新增预约排版
     * 
     * @param scheduled 预约排版
     * @return 结果
     */
    @Override
    public int insertScheduled(Scheduled scheduled)
    {
        // 如果scheduledId为空，则生成一个基于时间戳的ID
        if (scheduled.getScheduledId() == null) {
            scheduled.setScheduledId(System.currentTimeMillis());
        }
        scheduled.setCreateTime(DateUtils.getNowDate());
        return scheduledMapper.insertScheduled(scheduled);
    }

    /**
     * 修改预约排版
     * 
     * @param scheduled 预约排版
     * @return 结果
     */
    @Override
    public int updateScheduled(Scheduled scheduled)
    {
        scheduled.setUpdateTime(DateUtils.getNowDate());
        return scheduledMapper.updateScheduled(scheduled);
    }

    /**
     * 批量删除预约排版
     * 
     * @param scheduledIds 需要删除的预约排版主键
     * @return 结果
     */
    @Override
    public int deleteScheduledByScheduledIds(Long[] scheduledIds)
    {
        return scheduledMapper.deleteScheduledByScheduledIds(scheduledIds);
    }

    /**
     * 删除预约排版信息
     * 
     * @param scheduledId 预约排版主键
     * @return 结果
     */
    @Override
    public int deleteScheduledByScheduledId(Long scheduledId)
    {
        return scheduledMapper.deleteScheduledByScheduledId(scheduledId);
    }

    @Override
    public List<ScheduledVo> getCheduledList(ScheduledVo scheduled) {
        return scheduledMapper.getCheduledList(scheduled);
    }
}
