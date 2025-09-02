package com.doctor.mood.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.doctor.mood.mapper.MoodScaleMapper;
import com.doctor.mood.mapper.MoodQuestionMapper;
import com.doctor.mood.domain.entity.MoodScale;
import com.doctor.mood.service.IMoodScaleService;

/**
 * 心理情绪量表Service业务层处理
 */
@Service
public class MoodScaleServiceImpl implements IMoodScaleService 
{
    @Autowired
    private MoodScaleMapper moodScaleMapper;
    
    @Autowired
    private MoodQuestionMapper moodQuestionMapper;

    /**
     * 查询心理情绪量表
     * 
     * @param scaleId 心理情绪量表主键
     * @return 心理情绪量表
     */
    @Override
    public MoodScale selectMoodScaleByScaleId(Long scaleId)
    {
        return moodScaleMapper.selectMoodScaleByScaleId(scaleId);
    }

    /**
     * 查询心理情绪量表列表
     * 
     * @param moodScale 心理情绪量表
     * @return 心理情绪量表
     */
    @Override
    public List<MoodScale> selectMoodScaleList(MoodScale moodScale)
    {
        return moodScaleMapper.selectMoodScaleList(moodScale);
    }

    /**
     * 新增心理情绪量表
     * 
     * @param moodScale 心理情绪量表
     * @return 结果
     */
    @Override
    public int insertMoodScale(MoodScale moodScale)
    {
        return moodScaleMapper.insertMoodScale(moodScale);
    }

    /**
     * 修改心理情绪量表
     * 
     * @param moodScale 心理情绪量表
     * @return 结果
     */
    @Override
    public int updateMoodScale(MoodScale moodScale)
    {
        return moodScaleMapper.updateMoodScale(moodScale);
    }

    /**
     * 批量删除心理情绪量表
     * 
     * @param scaleIds 需要删除的心理情绪量表主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteMoodScaleByScaleIds(Long[] scaleIds)
    {
        // 删除量表关联的所有问题
        for (Long scaleId : scaleIds)
        {
            moodQuestionMapper.deleteMoodQuestionByScaleId(scaleId);
        }
        return moodScaleMapper.deleteMoodScaleByScaleIds(scaleIds);
    }

    /**
     * 删除心理情绪量表信息
     * 
     * @param scaleId 心理情绪量表主键
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteMoodScaleByScaleId(Long scaleId)
    {
        // 删除量表关联的所有问题
        moodQuestionMapper.deleteMoodQuestionByScaleId(scaleId);
        return moodScaleMapper.deleteMoodScaleByScaleId(scaleId);
    }
} 