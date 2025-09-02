package com.doctor.chat.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doctor.chat.entity.ChatMessageClear;
import com.doctor.chat.mapper.ChatMessageClearMapper;
import com.doctor.chat.service.ChatMessageClearService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

/**
 * 聊天记录清空记录Service实现
 */
@Service
@Slf4j
public class ChatMessageClearServiceImpl extends ServiceImpl<ChatMessageClearMapper, ChatMessageClear> implements ChatMessageClearService {

    @Autowired
    private ChatMessageClearMapper chatMessageClearMapper;
    
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean recordClearHistory(Long userId, Long friendId, String conversationId) {
        try {
            if (userId == null || friendId == null || conversationId == null) {
                log.error("记录清空聊天记录失败: 参数不能为空 userId={}, friendId={}, conversationId={}", userId, friendId, conversationId);
                return false;
            }
            
            log.info("记录清空聊天记录: userId={}, friendId={}, conversationId={}", userId, friendId, conversationId);
            
            // 创建记录对象
            ChatMessageClear record = new ChatMessageClear();
            record.setUserId(userId);
            record.setFriendId(friendId);
            record.setConversationId(conversationId);
            record.setClearTime(new Date());
            
            // 打印要保存的对象内容
            log.info("准备保存清空记录: {}", record);
            
            // 尝试删除可能存在的旧记录，避免重复
            try {
                chatMessageClearMapper.deleteOldClearRecords(userId, friendId);
                log.info("成功删除旧的清空记录");
            } catch (Exception e) {
                log.warn("删除旧的清空记录时出错，继续执行新增: {}", e.getMessage());
            }
            
            // 保存新记录
            boolean saved = save(record);
            if (!saved) {
                log.error("保存清空记录失败");
                return false;
            }
            
            log.info("成功保存清空记录，ID: {}", record.getClearId());
            return true;
        } catch (DataAccessException e) {
            log.error("数据库访问错误，记录清空聊天记录失败: {}", e.getMessage(), e);
            return false;
        } catch (Exception e) {
            log.error("记录清空聊天记录失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public Date getLastClearTime(Long userId, Long friendId) {
        try {
            if (userId == null || friendId == null) {
                log.error("获取清空时间失败: 参数不能为空");
                return null;
            }
            
            log.info("获取最近一次清空时间: userId={}, friendId={}", userId, friendId);
            Date clearTime = chatMessageClearMapper.getLastClearTime(userId, friendId);
            log.info("查询到的清空时间: {}", clearTime);
            return clearTime;
        } catch (DataAccessException e) {
            log.error("数据库访问错误，获取清空时间失败: {}", e.getMessage(), e);
            return null;
        } catch (Exception e) {
            log.error("获取清空时间失败: {}", e.getMessage(), e);
            return null;
        }
    }
} 