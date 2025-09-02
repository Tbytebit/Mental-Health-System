package com.doctor.web.service.impl;

import java.util.Date;
import java.util.List;

import com.doctor.web.domain.Feedback;
import com.doctor.web.mapper.FeedbackMapper;
import com.doctor.web.service.IFeedbackService;
import com.doctor.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户反馈Service业务层处理
 * 
 * @author LI
 * @date 2024-05-09
 */
@Service
public class FeedbackServiceImpl implements IFeedbackService
{
    @Autowired
    private FeedbackMapper feedbackMapper;

    /**
     * 查询用户反馈
     * 
     * @param feedbackId 用户反馈主键
     * @return 用户反馈
     */
    @Override
    public Feedback selectFeedbackByFeedbackId(Long feedbackId)
    {
        return feedbackMapper.selectFeedbackByFeedbackId(feedbackId);
    }

    /**
     * 查询用户反馈列表
     * 
     * @param feedback 用户反馈
     * @return 用户反馈
     */
    @Override
    public List<Feedback> selectFeedbackList(Feedback feedback)
    {
        return feedbackMapper.selectFeedbackList(feedback);
    }

    /**
     * 新增用户反馈
     * 
     * @param feedback 用户反馈
     * @return 结果
     */
    @Override
    public int insertFeedback(Feedback feedback)
    {
        feedback.setCreateTime(new Date());
        return feedbackMapper.insertFeedback(feedback);
    }

    /**
     * 修改用户反馈
     * 
     * @param feedback 用户反馈
     * @return 结果
     */
    @Override
    public int updateFeedback(Feedback feedback)
    {
        feedback.setUpdateTime(DateUtils.getNowDate());
        return feedbackMapper.updateFeedback(feedback);
    }

    /**
     * 批量删除用户反馈
     * 
     * @param feedbackIds 需要删除的用户反馈主键
     * @return 结果
     */
    @Override
    public int deleteFeedbackByFeedbackIds(Long[] feedbackIds)
    {
        return feedbackMapper.deleteFeedbackByFeedbackIds(feedbackIds);
    }

    /**
     * 删除用户反馈信息
     * 
     * @param feedbackId 用户反馈主键
     * @return 结果
     */
    @Override
    public int deleteFeedbackByFeedbackId(Long feedbackId)
    {
        return feedbackMapper.deleteFeedbackByFeedbackId(feedbackId);
    }
    /**
     * 查询今天反馈次数
     * @param userId
     * @return
     */
    @Override
    public Integer countDay(Long userId) {
        return feedbackMapper.countDay(userId);
    }
}
