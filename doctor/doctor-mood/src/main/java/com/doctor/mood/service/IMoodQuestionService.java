package com.doctor.mood.service;

import java.util.List;
import com.doctor.mood.domain.entity.MoodQuestion;

/**
 * 心理量表问题Service接口
 */
public interface IMoodQuestionService 
{
    /**
     * 查询心理量表问题
     * 
     * @param questionId 心理量表问题主键
     * @return 心理量表问题
     */
    public MoodQuestion selectMoodQuestionByQuestionId(Long questionId);

    /**
     * 查询心理量表问题列表
     * 
     * @param moodQuestion 心理量表问题
     * @return 心理量表问题集合
     */
    public List<MoodQuestion> selectMoodQuestionList(MoodQuestion moodQuestion);

    /**
     * 查询指定量表的所有问题
     * 
     * @param scaleId 量表ID
     * @return 问题列表
     */
    public List<MoodQuestion> selectMoodQuestionByScaleId(Long scaleId);

    /**
     * 新增心理量表问题
     * 
     * @param moodQuestion 心理量表问题
     * @return 结果
     */
    public int insertMoodQuestion(MoodQuestion moodQuestion);

    /**
     * 修改心理量表问题
     * 
     * @param moodQuestion 心理量表问题
     * @return 结果
     */
    public int updateMoodQuestion(MoodQuestion moodQuestion);

    /**
     * 批量删除心理量表问题
     * 
     * @param questionIds 需要删除的心理量表问题主键集合
     * @return 结果
     */
    public int deleteMoodQuestionByQuestionIds(Long[] questionIds);

    /**
     * 删除心理量表问题信息
     * 
     * @param questionId 心理量表问题主键
     * @return 结果
     */
    public int deleteMoodQuestionByQuestionId(Long questionId);
} 