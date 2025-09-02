package com.doctor.mood.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doctor.mood.mapper.MoodRecordMapper;
import com.doctor.mood.domain.entity.MoodRecord;
import com.doctor.mood.service.IMoodRecordService;

/**
 * 用户情绪量表填写记录Service业务层处理
 */
@Service
public class MoodRecordServiceImpl implements IMoodRecordService 
{
    @Autowired
    private MoodRecordMapper moodRecordMapper;

    /**
     * 查询用户情绪量表填写记录
     * 
     * @param recordId 用户情绪量表填写记录主键
     * @return 用户情绪量表填写记录
     */
    @Override
    public MoodRecord selectMoodRecordByRecordId(Long recordId)
    {
        return moodRecordMapper.selectMoodRecordByRecordId(recordId);
    }

    /**
     * 查询用户情绪量表填写记录列表
     * 
     * @param moodRecord 用户情绪量表填写记录
     * @return 用户情绪量表填写记录
     */
    @Override
    public List<MoodRecord> selectMoodRecordList(MoodRecord moodRecord)
    {
        return moodRecordMapper.selectMoodRecordList(moodRecord);
    }

    /**
     * 查询用户指定量表的填写记录
     *
     * @param userId 用户ID
     * @param scaleId 量表ID
     * @return 记录列表
     */
    @Override
    public List<MoodRecord> selectMoodRecordByUserIdAndScaleId(Long userId, Long scaleId)
    {
        return moodRecordMapper.selectMoodRecordByUserIdAndScaleId(userId, scaleId);
    }

    /**
     * 查询用户的所有量表填写记录
     *
     * @param userId 用户ID
     * @return 记录列表
     */
    @Override
    public List<MoodRecord> selectMoodRecordByUserId(Long userId)
    {
        return moodRecordMapper.selectMoodRecordByUserId(userId);
    }

    /**
     * 新增用户情绪量表填写记录
     * 
     * @param moodRecord 用户情绪量表填写记录
     * @return 结果
     */
    @Override
    public int insertMoodRecord(MoodRecord moodRecord)
    {
        return moodRecordMapper.insertMoodRecord(moodRecord);
    }

    /**
     * 修改用户情绪量表填写记录
     * 
     * @param moodRecord 用户情绪量表填写记录
     * @return 结果
     */
    @Override
    public int updateMoodRecord(MoodRecord moodRecord)
    {
        return moodRecordMapper.updateMoodRecord(moodRecord);
    }

    /**
     * 批量删除用户情绪量表填写记录
     * 
     * @param recordIds 需要删除的用户情绪量表填写记录主键
     * @return 结果
     */
    @Override
    public int deleteMoodRecordByRecordIds(Long[] recordIds)
    {
        return moodRecordMapper.deleteMoodRecordByRecordIds(recordIds);
    }

    /**
     * 删除用户情绪量表填写记录信息
     * 
     * @param recordId 用户情绪量表填写记录主键
     * @return 结果
     */
    @Override
    public int deleteMoodRecordByRecordId(Long recordId)
    {
        return moodRecordMapper.deleteMoodRecordByRecordId(recordId);
    }
}