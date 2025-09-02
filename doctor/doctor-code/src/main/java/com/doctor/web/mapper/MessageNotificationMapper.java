package com.doctor.web.mapper;

import java.util.List;
import com.doctor.web.domain.MessageNotification;

/**
 * 消息通知Mapper接口
 * 
 * @author LI
 * @date 2024-05-09
 */
public interface MessageNotificationMapper 
{
    /**
     * 查询消息通知
     * 
     * @param messageId 消息通知主键
     * @return 消息通知
     */
    public MessageNotification selectMessageNotificationByMessageId(Long messageId);

    /**
     * 查询消息通知列表
     * 
     * @param messageNotification 消息通知
     * @return 消息通知集合
     */
    public List<MessageNotification> selectMessageNotificationList(MessageNotification messageNotification);

    /**
     * 新增消息通知
     * 
     * @param messageNotification 消息通知
     * @return 结果
     */
    public int insertMessageNotification(MessageNotification messageNotification);

    /**
     * 修改消息通知
     * 
     * @param messageNotification 消息通知
     * @return 结果
     */
    public int updateMessageNotification(MessageNotification messageNotification);

    /**
     * 删除消息通知
     * 
     * @param messageId 消息通知主键
     * @return 结果
     */
    public int deleteMessageNotificationByMessageId(Long messageId);

    /**
     * 批量删除消息通知
     * 
     * @param messageIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteMessageNotificationByMessageIds(Long[] messageIds);

    /**
     * 查询用户消息列表
     * @param messageNotification
     * @return
     */
    List<MessageNotification> getUserMsgList(MessageNotification messageNotification);
}
