package com.doctor.web.service.impl;

import java.util.List;

import com.doctor.web.domain.MessageNotification;
import com.doctor.web.mapper.MessageNotificationMapper;
import com.doctor.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doctor.web.service.IMessageNotificationService;

/**
 * 消息通知Service业务层处理
 * 
 * @author LI
 * @date 2024-05-09
 */
@Service
public class MessageNotificationServiceImpl implements IMessageNotificationService 
{
    @Autowired
    private MessageNotificationMapper messageNotificationMapper;

    /**
     * 查询消息通知
     * 
     * @param messageId 消息通知主键
     * @return 消息通知
     */
    @Override
    public MessageNotification selectMessageNotificationByMessageId(Long messageId)
    {
        return messageNotificationMapper.selectMessageNotificationByMessageId(messageId);
    }

    /**
     * 查询消息通知列表
     * 
     * @param messageNotification 消息通知
     * @return 消息通知
     */
    @Override
    public List<MessageNotification> selectMessageNotificationList(MessageNotification messageNotification)
    {
        return messageNotificationMapper.selectMessageNotificationList(messageNotification);
    }

    /**
     * 新增消息通知
     * 
     * @param messageNotification 消息通知
     * @return 结果
     */
    @Override
    public int insertMessageNotification(MessageNotification messageNotification)
    {
        messageNotification.setCreateTime(DateUtils.getNowDate());
        return messageNotificationMapper.insertMessageNotification(messageNotification);
    }

    /**
     * 修改消息通知
     * 
     * @param messageNotification 消息通知
     * @return 结果
     */
    @Override
    public int updateMessageNotification(MessageNotification messageNotification)
    {
        messageNotification.setUpdateTime(DateUtils.getNowDate());
        return messageNotificationMapper.updateMessageNotification(messageNotification);
    }

    /**
     * 批量删除消息通知
     * 
     * @param messageIds 需要删除的消息通知主键
     * @return 结果
     */
    @Override
    public int deleteMessageNotificationByMessageIds(Long[] messageIds)
    {
        return messageNotificationMapper.deleteMessageNotificationByMessageIds(messageIds);
    }

    /**
     * 删除消息通知信息
     * 
     * @param messageId 消息通知主键
     * @return 结果
     */
    @Override
    public int deleteMessageNotificationByMessageId(Long messageId)
    {
        return messageNotificationMapper.deleteMessageNotificationByMessageId(messageId);
    }
    /**
     * 查询用户消息列表
     * @param messageNotification
     * @return
     */
    @Override
    public List<MessageNotification> getUserMsgList(MessageNotification messageNotification) {
        return messageNotificationMapper.getUserMsgList(messageNotification);
    }
}
