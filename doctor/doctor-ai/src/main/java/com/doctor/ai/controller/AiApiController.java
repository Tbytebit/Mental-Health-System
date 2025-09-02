package com.doctor.ai.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.doctor.ai.domain.AiAssistant;
import com.doctor.ai.domain.AiChat;
import com.doctor.ai.domain.AiMessage;
import com.doctor.ai.service.IAiAssistantService;
import com.doctor.ai.service.IAiChatService;
import com.doctor.common.core.controller.BaseController;
import com.doctor.common.core.domain.AjaxResult;
import com.doctor.common.core.domain.model.LoginUser;
import com.doctor.common.utils.SecurityUtils;
import com.doctor.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * AI助手API接口
 */
@RestController
@RequestMapping("/api/ai")
public class AiApiController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(AiApiController.class);

    @Autowired
    private IAiAssistantService aiAssistantService;

    @Autowired
    private IAiChatService aiChatService;

    /**
     * 获取AI助手列表
     */
    @GetMapping("/assistant/list")
    public AjaxResult getAssistantList()
    {
        List<AiAssistant> list = aiAssistantService.selectActiveAiAssistants();
        return success(list);
    }

    /**
     * 获取聊天历史记录
     */
    @GetMapping("/history")
    public AjaxResult getChatHistory(@RequestParam(required = false) Long chatId,
                                     @RequestParam(required = false) Long beforeId,
                                     @RequestParam(required = false, defaultValue = "20") Integer limit)
    {
        Long userId = SecurityUtils.getUserId();
        if (chatId == null) {
            // 返回用户的聊天列表
            List<AiChat> chatList = aiChatService.selectAiChatListByUserId(userId);
            return success(chatList);
        } else {
            // 返回指定聊天的消息历史
            List<AiMessage> messageList = aiChatService.getChatHistory(chatId, beforeId, limit);
            Map<String, Object> result = new HashMap<>();
            result.put("chatId", chatId);
            result.put("messages", messageList);
            return success(result);
        }
    }

    /**
     * 创建新的AI聊天
     */
    @PostMapping("/create")
    public AjaxResult createChat(@RequestBody AiChat aiChat)
    {
        Long userId = SecurityUtils.getUserId();
        aiChat.setUserId(userId);
        
        AiChat newChat = aiChatService.insertAiChat(aiChat);
        Map<String, Object> result = new HashMap<>();
        result.put("chatId", newChat.getChatId());
        result.put("title", newChat.getTitle());
        
        return success(result);
    }

    /**
     * 删除AI聊天
     */
    @DeleteMapping("/delete/{chatId}")
    public AjaxResult deleteChat(@PathVariable Long chatId)
    {
        return toAjax(aiChatService.deleteAiChatById(chatId));
    }

    /**
     * 删除单个消息
     */
    @DeleteMapping("/message/{messageId}")
    public AjaxResult deleteMessage(@PathVariable Long messageId)
    {
        Long userId = SecurityUtils.getUserId();
        return toAjax(aiChatService.deleteAiMessageById(messageId, userId));
    }

    /**
     * 清空特定聊天的历史记录
     */
    @PostMapping("/chat/{chatId}/clear")
    public AjaxResult clearChatHistory(@PathVariable Long chatId)
    {
        Long userId = SecurityUtils.getUserId();
        return toAjax(aiChatService.clearChatHistory(chatId, userId));
    }

    /**
     * 清空所有聊天历史记录
     */
    @PostMapping("/chat/clear-all")
    public AjaxResult clearAllChatHistory()
    {
        try {
            Long userId = SecurityUtils.getUserId();
            int result = aiChatService.clearAllChatHistory(userId);
            return success("已清空所有聊天历史，共删除 " + result + " 条消息");
        } catch (Exception e) {
            log.error("清空所有聊天历史时发生错误: " + e.getMessage(), e);
            return error("清空历史记录失败: " + e.getMessage());
        }
    }

    /**
     * 发送消息给AI并获取回复
     */
    @PostMapping("/message/send")
    public AjaxResult sendMessage(@RequestBody Map<String, Object> params)
    {
        Long userId = SecurityUtils.getUserId();
        Long chatId = null;
        if (params.get("chatId") != null && StringUtils.isNotBlank(params.get("chatId").toString())) {
            try {
                chatId = Long.parseLong(params.get("chatId").toString());
            } catch (NumberFormatException e) {
                // 忽略解析错误
            }
        }
        
        Integer assistantType = Integer.parseInt(params.get("assistantType").toString());
        String content = params.get("content").toString();
        
        // 发送消息给AI并获取回复
        AiMessage aiMessage = aiChatService.sendMessageToAI(userId, chatId, assistantType, content);
        
        // 如果是新创建的聊天，返回chatId
        if (chatId == null || chatId == 0) {
            params.put("chatId", aiMessage.getChatId());
        }
        
        Map<String, Object> result = new HashMap<>();
        result.put("id", aiMessage.getMessageId());
        result.put("chatId", aiMessage.getChatId());
        result.put("content", aiMessage.getContent());
        result.put("type", aiMessage.getMessageType());
        result.put("time", aiMessage.getCreateTime());
        
        return success(result);
    }
} 