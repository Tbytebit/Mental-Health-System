package com.doctor.mood.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doctor.mood.mapper.MoodQuestionMapper;
import com.doctor.mood.domain.entity.MoodQuestion;
import com.doctor.mood.service.IMoodQuestionService;

/**
 * 心理量表问题Service业务层处理
 */
@Service
public class MoodQuestionServiceImpl implements IMoodQuestionService 
{
    @Autowired
    private MoodQuestionMapper moodQuestionMapper;

    /**
     * 查询心理量表问题
     * 
     * @param questionId 心理量表问题主键
     * @return 心理量表问题
     */
    @Override
    public MoodQuestion selectMoodQuestionByQuestionId(Long questionId)
    {
        return moodQuestionMapper.selectMoodQuestionByQuestionId(questionId);
    }

    /**
     * 查询心理量表问题列表
     * 
     * @param moodQuestion 心理量表问题
     * @return 心理量表问题
     */
    @Override
    public List<MoodQuestion> selectMoodQuestionList(MoodQuestion moodQuestion)
    {
        return moodQuestionMapper.selectMoodQuestionList(moodQuestion);
    }

    /**
     * 查询指定量表的所有问题
     *
     * @param scaleId 量表ID
     * @return 问题列表
     */
    @Override
    public List<MoodQuestion> selectMoodQuestionByScaleId(Long scaleId)
    {
        return moodQuestionMapper.selectMoodQuestionByScaleId(scaleId);
    }

    /**
     * 新增心理量表问题
     * 
     * @param moodQuestion 心理量表问题
     * @return 结果
     */
    @Override
    public int insertMoodQuestion(MoodQuestion moodQuestion)
    {
        return moodQuestionMapper.insertMoodQuestion(moodQuestion);
    }

    /**
     * 修改心理量表问题
     * 
     * @param moodQuestion 心理量表问题
     * @return 结果
     */
    @Override
    public int updateMoodQuestion(MoodQuestion moodQuestion)
    {
        return moodQuestionMapper.updateMoodQuestion(moodQuestion);
    }

    /**
     * 批量删除心理量表问题
     * 
     * @param questionIds 需要删除的心理量表问题主键
     * @return 结果
     */
    @Override
    public int deleteMoodQuestionByQuestionIds(Long[] questionIds)
    {
        return moodQuestionMapper.deleteMoodQuestionByQuestionIds(questionIds);
    }

    /**
     * 删除心理量表问题信息
     * 
     * @param questionId 心理量表问题主键
     * @return 结果
     */
    @Override
    public int deleteMoodQuestionByQuestionId(Long questionId)
    {
        return moodQuestionMapper.deleteMoodQuestionByQuestionId(questionId);
    }
} 