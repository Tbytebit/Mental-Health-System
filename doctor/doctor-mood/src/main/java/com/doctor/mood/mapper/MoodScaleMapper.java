package com.doctor.mood.mapper;

import java.util.List;
import com.doctor.mood.domain.entity.MoodScale;

/**
 * 心理情绪量表Mapper接口
 */
public interface MoodScaleMapper 
{
    /**
     * 查询心理情绪量表
     * 
     * @param scaleId 心理情绪量表主键
     * @return 心理情绪量表
     */
    public MoodScale selectMoodScaleByScaleId(Long scaleId);

    /**
     * 查询心理情绪量表列表
     * 
     * @param moodScale 心理情绪量表
     * @return 心理情绪量表集合
     */
    public List<MoodScale> selectMoodScaleList(MoodScale moodScale);

    /**
     * 新增心理情绪量表
     * 
     * @param moodScale 心理情绪量表
     * @return 结果
     */
    public int insertMoodScale(MoodScale moodScale);

    /**
     * 修改心理情绪量表
     * 
     * @param moodScale 心理情绪量表
     * @return 结果
     */
    public int updateMoodScale(MoodScale moodScale);

    /**
     * 删除心理情绪量表
     * 
     * @param scaleId 心理情绪量表主键
     * @return 结果
     */
    public int deleteMoodScaleByScaleId(Long scaleId);

    /**
     * 批量删除心理情绪量表
     * 
     * @param scaleIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMoodScaleByScaleIds(Long[] scaleIds);
} 