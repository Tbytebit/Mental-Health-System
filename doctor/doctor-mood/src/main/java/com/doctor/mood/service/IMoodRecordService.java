package com.doctor.mood.service;

import java.util.List;
import com.doctor.mood.domain.entity.MoodRecord;

/**
 * 用户情绪量表填写记录Service接口
 */
public interface IMoodRecordService 
{
    /**
     * 查询用户情绪量表填写记录
     * 
     * @param recordId 用户情绪量表填写记录主键
     * @return 用户情绪量表填写记录
     */
    public MoodRecord selectMoodRecordByRecordId(Long recordId);

    /**
     * 查询用户情绪量表填写记录列表
     * 
     * @param moodRecord 用户情绪量表填写记录
     * @return 用户情绪量表填写记录集合
     */
    public List<MoodRecord> selectMoodRecordList(MoodRecord moodRecord);

    /**
     * 查询用户指定量表的填写记录
     * 
     * @param userId 用户ID
     * @param scaleId 量表ID
     * @return 记录列表
     */
    public List<MoodRecord> selectMoodRecordByUserIdAndScaleId(Long userId, Long scaleId);

    /**
     * 查询用户的所有量表填写记录
     * 
     * @param userId 用户ID
     * @return 记录列表
     */
    public List<MoodRecord> selectMoodRecordByUserId(Long userId);

    /**
     * 新增用户情绪量表填写记录
     * 
     * @param moodRecord 用户情绪量表填写记录
     * @return 结果
     */
    public int insertMoodRecord(MoodRecord moodRecord);

    /**
     * 修改用户情绪量表填写记录
     * 
     * @param moodRecord 用户情绪量表填写记录
     * @return 结果
     */
    public int updateMoodRecord(MoodRecord moodRecord);

    /**
     * 批量删除用户情绪量表填写记录
     * 
     * @param recordIds 需要删除的用户情绪量表填写记录主键集合
     * @return 结果
     */
    public int deleteMoodRecordByRecordIds(Long[] recordIds);

    /**
     * 删除用户情绪量表填写记录信息
     * 
     * @param recordId 用户情绪量表填写记录主键
     * @return 结果
     */
    public int deleteMoodRecordByRecordId(Long recordId);
} 