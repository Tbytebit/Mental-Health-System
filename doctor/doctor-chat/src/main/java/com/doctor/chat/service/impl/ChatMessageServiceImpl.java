package com.doctor.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doctor.chat.dto.ChatMessageDTO;
import com.doctor.chat.entity.ChatMessage;
import com.doctor.chat.mapper.ChatMessageMapper;
import com.doctor.chat.service.ChatMessageClearService;
import com.doctor.chat.service.ChatMessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 聊天消息Service实现
 */
@Service
@Slf4j
public class ChatMessageServiceImpl extends ServiceImpl<ChatMessageMapper, ChatMessage> implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;
    
    @Autowired
    private ChatMessageClearService chatMessageClearService;

    @Override
    public boolean sendMessage(ChatMessage chatMessage) {
        // 设置创建时间
        if (chatMessage.getCreateTime() == null) {
            chatMessage.setCreateTime(new Date());
        }
        chatMessage.setUpdateTime(new Date());
        
        // 默认消息状态为未读
        if (chatMessage.getStatus() == null) {
            chatMessage.setStatus(0);
        }
        
        // 生成会话ID（较小的用户ID在前）
        if (chatMessage.getConversationId() == null || chatMessage.getConversationId().isEmpty()) {
            Long senderId = chatMessage.getSenderId();
            Long receiverId = chatMessage.getReceiverId();
            
            String conversationId;
            if (senderId < receiverId) {
                conversationId = senderId + "_" + receiverId;
            } else {
                conversationId = receiverId + "_" + senderId;
            }
            
            chatMessage.setConversationId(conversationId);
        }
        
        log.info("保存聊天消息: {}", chatMessage);
        return save(chatMessage);
    }

    @Override
    public boolean save(ChatMessage entity) {
        try {
            log.info("执行save方法保存消息: {}", entity);
            return super.save(entity);
        } catch (Exception e) {
            log.error("保存消息失败: ", e);
            throw e;
        }
    }

    /*
    @Override
    public List<ChatMessageDTO> getChatHistory(Long userId1, Long userId2, Integer limit) {
        // 获取清空时间，只有查询者是userId1时才考虑清空时间
        Date clearTime = chatMessageClearService.getLastClearTime(userId1, userId2);
        List<ChatMessage> messageList = chatMessageMapper.getChatHistory(userId1, userId2, limit, clearTime);
        return convertToDTO(messageList);
    }
    */
    @Override
    public List<ChatMessageDTO> getChatHistory(Long userId1, Long userId2, Long requesterId, Integer limit) {
        // 获取请求者的清空时间
        Date clearTime = chatMessageClearService.getLastClearTime(requesterId,
                requesterId.equals(userId1) ? userId2 : userId1);
        List<ChatMessage> messageList = chatMessageMapper.getChatHistory(userId1, userId2, limit, clearTime);
        return convertToDTO(messageList);
    }

    @Override
    public int getUnreadCount(Long receiverId) {
        return chatMessageMapper.getUnreadCount(receiverId);
    }

    @Override
    public boolean markMessageRead(Long senderId, Long receiverId) {
        try {
            log.info("开始标记消息已读: senderId={}, receiverId={}", senderId, receiverId);
            
            // 参数校验
            if (senderId == null || receiverId == null) {
                log.error("标记消息已读失败: 参数不能为空");
                return false;
            }
            
            // 直接使用原生SQL更新而不是Lambda表达式
            int updatedRows = chatMessageMapper.markMessageRead(senderId, receiverId);
            log.info("已标记消息已读, 更新了{}条记录", updatedRows);
            
            // 无论是否有更新，都返回成功
            return true;
        } catch (Exception e) {
            log.error("标记消息已读失败: {}", e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean markMessageReadByConversation(String conversationId, Long receiverId) {
        try {
            LambdaUpdateWrapper<ChatMessage> updateWrapper = new LambdaUpdateWrapper<>();
            updateWrapper.eq(ChatMessage::getConversationId, conversationId)
                    .eq(ChatMessage::getReceiverId, receiverId)
                    .eq(ChatMessage::getStatus, 0)
                    .set(ChatMessage::getStatus, 1)
                    .set(ChatMessage::getUpdateTime, new Date());
            
            chatMessageMapper.update(null, updateWrapper);
            return true;
        } catch (Exception e) {
            log.error("标记会话消息已读失败", e);
            return false;
        }
    }

    @Override
    public List<ChatMessage> getUnreadMessages(Long userId) {
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getReceiverId, userId)
                .eq(ChatMessage::getStatus, 0)
                .orderByDesc(ChatMessage::getCreateTime);
        
        return chatMessageMapper.selectList(queryWrapper);
    }

    @Override
    public int countUnreadMessages(Long userId) {
        return chatMessageMapper.getUnreadCount(userId);
    }

    @Override
    public int countUnreadMessagesByConversation(Long userId, String conversationId) {
        // 使用新增的countUnreadMessagesByConversation方法
        return chatMessageMapper.countUnreadMessagesByConversation(userId, conversationId);
    }

    @Override
    public List<ChatMessageDTO> getMessagesByConversationId(String conversationId, Integer limit) {
        // 这里需要从会话ID解析出用户ID，才能确定是否考虑清空时间
        // 假设调用方传入了当前用户ID作为额外参数，或者在别处做处理
        List<ChatMessage> messageList = chatMessageMapper.getMessagesByConversationId(conversationId, limit, null);
        return convertToDTO(messageList);
    }

    @Override
    public List<ChatMessage> getChatHistoryEntities(Long userId1, Long userId2, Integer limit) {
        // 构建会话ID
        String conversationId;
        if (userId1 < userId2) {
            conversationId = userId1 + "_" + userId2;
        } else {
            conversationId = userId2 + "_" + userId1;
        }
        
        // 获取清空时间
        Date clearTime = chatMessageClearService.getLastClearTime(userId1, userId2);
        
        // 构建查询条件
        LambdaQueryWrapper<ChatMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ChatMessage::getConversationId, conversationId);
        
        // 如果有清空时间，只返回清空时间之后的消息
        if (clearTime != null) {
            queryWrapper.gt(ChatMessage::getCreateTime, clearTime);
        }
        
        if (limit != null && limit > 0) {
            queryWrapper.last("LIMIT " + limit);
        }
        queryWrapper.orderByDesc(ChatMessage::getCreateTime);
        
        return chatMessageMapper.selectList(queryWrapper);
    }
    
    @Override
    public boolean clearChatMessages(Long userId, Long friendId) {
        try {
            if (userId == null || friendId == null) {
                log.error("清空聊天记录失败: 参数不能为空 userId={}, friendId={}", userId, friendId);
                return false;
            }
            
            log.info("开始清空聊天记录: userId={}, friendId={}", userId, friendId);
            
            // 构建会话ID
            String conversationId;
            if (userId < friendId) {
                conversationId = userId + "_" + friendId;
            } else {
                conversationId = friendId + "_" + userId;
            }
            log.info("构建的会话ID: {}", conversationId);
            
            // 不再删除消息，而是记录清空操作
            log.info("调用记录清空操作服务...");
            boolean result = chatMessageClearService.recordClearHistory(userId, friendId, conversationId);
            
            if (result) {
                log.info("已成功记录清空聊天记录: userId={}, friendId={}", userId, friendId);
            } else {
                log.error("记录清空聊天记录失败: userId={}, friendId={}", userId, friendId);
                // 检查ChatMessageClearService是否正确注入
                if (chatMessageClearService == null) {
                    log.error("ChatMessageClearService未正确注入");
                }
            }
            
            return result;
        } catch (Exception e) {
            log.error("清空聊天记录失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 将消息实体转换为DTO
     *
     * @param messageList 消息实体列表
     * @return DTO列表
     */
    private List<ChatMessageDTO> convertToDTO(List<ChatMessage> messageList) {
        List<ChatMessageDTO> dtoList = new ArrayList<>();
        for (ChatMessage message : messageList) {
            ChatMessageDTO dto = new ChatMessageDTO();
            BeanUtils.copyProperties(message, dto);
            // TODO: 实际项目中需要根据ID查询用户信息补充发送者和接收者的名称、头像等信息
            dtoList.add(dto);
        }
        return dtoList;
    }
} 