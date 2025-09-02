package com.doctor.ai.service.impl;

import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.doctor.ai.service.IAiAssistantService;
import com.doctor.common.core.domain.model.LoginUser;
import com.doctor.common.utils.SecurityUtils;
import com.doctor.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.doctor.ai.domain.AiChat;
import com.doctor.ai.domain.AiMessage;
import com.doctor.ai.mapper.AiChatMapper;
import com.doctor.ai.mapper.AiMessageMapper;
import com.doctor.ai.service.IAiChatService;
import com.doctor.ai.util.DashScopeClient;

import javax.annotation.Resource;

/**
 * AI聊天服务实现
 */
@Service
public class AiChatServiceImpl implements IAiChatService
{
    private static final Logger log = LoggerFactory.getLogger(AiChatServiceImpl.class);

    @Resource
    private AiChatMapper aiChatMapper;

    @Resource
    private AiMessageMapper aiMessageMapper;

    @Resource
    private IAiAssistantService aiAssistantService;

    @Value("${ai.dashscope.apiKey:****}")
    private String apiKey;

    @Value("${ai.dashscope.model:qwen-plus}")
    private String model;

    /**
     * 查询AI聊天
     * 
     * @param chatId 聊天ID
     * @return AI聊天
     */
    @Override
    public AiChat selectAiChatById(Long chatId)
    {
        return aiChatMapper.selectAiChatById(chatId);
    }

    /**
     * 查询用户的AI聊天列表
     * 
     * @param userId 用户ID
     * @return AI聊天集合
     */
    @Override
    public List<AiChat> selectAiChatListByUserId(Long userId)
    {
        return aiChatMapper.selectAiChatListByUserId(userId);
    }

    /**
     * 创建AI聊天
     * 
     * @param aiChat 聊天信息
     * @return 结果
     */
    @Override
    public AiChat insertAiChat(AiChat aiChat)
    {
        // 设置默认值
        if (aiChat.getTitle() == null) {
            aiChat.setTitle("新对话");
        }
        aiChat.setLastMessageTime(new Date());
        aiChat.setDelFlag("0");
        aiChatMapper.insertAiChat(aiChat);
        return aiChat;
    }

    /**
     * 删除AI聊天
     * 
     * @param chatId 聊天ID
     * @return 结果
     */
    @Override
    public int deleteAiChatById(Long chatId)
    {
        // 先检查是否存在且属于当前用户
        AiChat chat = aiChatMapper.selectAiChatById(chatId);
        if (chat == null || !chat.getUserId().equals(SecurityUtils.getUserId())) {
            return 0;
        }
        // 删除相关消息
        aiMessageMapper.deleteMessagesByChatId(chatId);
        // 删除聊天
        return aiChatMapper.deleteAiChatById(chatId);
    }

    /**
     * 删除单个AI消息
     * 
     * @param messageId 消息ID
     * @param userId 用户ID，用于权限验证
     * @return 结果
     */
    @Override
    public int deleteAiMessageById(Long messageId, Long userId)
    {
        // 先检查消息是否存在且属于当前用户
        AiMessage message = aiMessageMapper.selectAiMessageById(messageId);
        if (message == null || !message.getUserId().equals(userId)) {
            return 0;
        }
        
        // 删除单个消息
        return aiMessageMapper.deleteAiMessageById(messageId);
    }

    /**
     * 清空特定聊天的历史记录
     * 
     * @param chatId 聊天ID
     * @param userId 用户ID，用于权限验证
     * @return 结果
     */
    @Override
    public int clearChatHistory(Long chatId, Long userId)
    {
        // 先检查聊天是否存在且属于当前用户
        AiChat chat = aiChatMapper.selectAiChatById(chatId);
        if (chat == null || !chat.getUserId().equals(userId)) {
            return 0;
        }
        
        // 删除聊天下的所有消息
        return aiMessageMapper.deleteMessagesByChatId(chatId);
    }

    /**
     * 清空用户所有聊天的历史记录
     * 
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    public int clearAllChatHistory(Long userId)
    {
        try {
            log.info("开始清空用户 {} 的所有聊天历史记录", userId);
            
            // 获取用户的所有聊天
            List<AiChat> chats = aiChatMapper.selectAiChatListByUserId(userId);
            log.info("查询到用户 {} 的聊天记录 {} 条", userId, chats != null ? chats.size() : 0);
            
            if (chats == null || chats.isEmpty()) {
                log.info("用户 {} 没有聊天记录，无需清空", userId);
                return 0;
            }
            
            int result = 0;
            
            // 遍历所有聊天，先删除其消息，再删除聊天记录
            for (AiChat chat : chats) {
                try {
                    // 删除消息
                    int messagesDeleted = aiMessageMapper.deleteMessagesByChatId(chat.getChatId());
                    log.info("删除聊天 {} 的消息 {} 条", chat.getChatId(), messagesDeleted);
                    result += messagesDeleted;
                    
                    // 删除聊天记录
                    int chatDeleted = aiChatMapper.deleteAiChatById(chat.getChatId());
                    log.info("删除聊天记录 {}, 结果: {}", chat.getChatId(), chatDeleted);
                } catch (Exception e) {
                    log.error("删除聊天 {} 及其消息时出错: {}", chat.getChatId(), e.getMessage(), e);
                }
            }
            
            log.info("清空用户 {} 的聊天历史完成，共删除 {} 条消息", userId, result);
            return result;
        } catch (Exception e) {
            log.error("清空所有聊天历史时发生错误", e);
            throw e; // 重新抛出异常，让上层能够捕获并处理
        }
    }

    /**
     * 获取聊天历史消息
     * 
     * @param chatId 聊天ID
     * @param beforeId 消息ID（获取此ID之前的消息）
     * @param limit 限制条数
     * @return 消息列表
     */
    @Override
    public List<AiMessage> getChatHistory(Long chatId, Long beforeId, Integer limit)
    {
        if (limit == null || limit <= 0) {
            limit = 20; // 默认获取20条
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("chatId", chatId);
        params.put("beforeId", beforeId);
        params.put("limit", limit);
        
        return aiMessageMapper.selectMessagesByChatId(params);
    }

    /**
     * 发送消息给AI并获取回复
     * 
     * @param userId 用户ID
     * @param chatId 聊天ID
     * @param assistantType 助手类型
     * @param content 消息内容
     * @return AI回复消息
     */
    @Override
    public AiMessage sendMessageToAI(Long userId, Long chatId, Integer assistantType, String content)
    {
        // 1. 如果chatId为空，创建新的聊天
        AiChat chat = null;
        if (chatId == null) {
            chat = new AiChat();
            chat.setUserId(userId);
            chat.setAssistantType(assistantType);
            chat.setTitle(StringUtils.truncate(content, 20)); // 使用消息内容前20个字符作为标题
            insertAiChat(chat);
            chatId = chat.getChatId();
        } else {
            chat = aiChatMapper.selectAiChatById(chatId);
            if (chat == null) {
                // 聊天不存在，创建新的
                chat = new AiChat();
                chat.setUserId(userId);
                chat.setAssistantType(assistantType);
                chat.setTitle(StringUtils.truncate(content, 20));
                insertAiChat(chat);
                chatId = chat.getChatId();
            } else {
                // 更新最后消息时间
                chat.setLastMessageTime(new Date());
                aiChatMapper.updateAiChat(chat);
            }
        }

        // 2. 保存用户消息 - 确保isUser字段为字符串"1"
        AiMessage userMessage = new AiMessage();
        userMessage.setChatId(chatId);
        userMessage.setUserId(userId);
        userMessage.setContent(content);
        userMessage.setIsUser("1"); // 1表示用户消息，确保使用字符串
        userMessage.setMessageType("text"); // 文本类型
        userMessage.setDelFlag("0");
        aiMessageMapper.insertAiMessage(userMessage);

        // 3. 获取消息历史作为上下文，进一步减少历史消息数量，控制TPM
        List<AiMessage> history = getChatHistory(chatId, null, 5); // 将历史消息减少到5条
        
        // 4. 获取系统提示词
        String systemPrompt = aiAssistantService.getSystemPrompt(assistantType);
        // 压缩系统提示词，减少token数量
        if (systemPrompt != null && systemPrompt.length() > 200) {
            systemPrompt = systemPrompt.substring(0, 200);
            log.debug("系统提示词过长，已截断");
        }
        
        // 5. 准备请求数据
        List<Map<String, String>> messages = new ArrayList<>();
        
        // 添加系统角色消息
        if (StringUtils.isNotEmpty(systemPrompt)) {
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", systemPrompt);
            messages.add(systemMessage);
        }
        
        // 消息合并和压缩处理：
        // 1. 如果历史消息过多，可能导致请求体过大，我们需要合并或丢弃一些旧消息
        // 2. 仅保留最近的对话，以确保上下文相关性和请求效率
        int maxContextMessages = 5; // 减少最大上下文消息数量
        List<AiMessage> contextMessages = new ArrayList<>();
        
        if (history.size() > maxContextMessages) {
            // 只保留最新的消息
            contextMessages = history.subList(history.size() - maxContextMessages, history.size());
            
            // 添加摘要消息，概括之前的对话
            Map<String, String> summaryMessage = new HashMap<>();
            summaryMessage.put("role", "system");
            summaryMessage.put("content", "以下是之前对话的继续");
            messages.add(summaryMessage);
        } else {
            contextMessages = history;
        }
        
        // 添加历史消息（从旧到新排序）
        for (int i = contextMessages.size() - 1; i >= 0; i--) {
            AiMessage msg = contextMessages.get(i);
            Map<String, String> historyMessage = new HashMap<>();
            historyMessage.put("role", "1".equals(msg.getIsUser()) ? "user" : "assistant");
            
            // 压缩消息内容，减少token数量
            String msgContent = msg.getContent();
            if (msgContent != null && msgContent.length() > 500) {
                msgContent = msgContent.substring(0, 500);
                log.debug("历史消息过长，已截断");
            }
            
            historyMessage.put("content", msgContent);
            messages.add(historyMessage);
        }
        
        // 合并连续的用户消息，减少API调用次数
        List<Map<String, String>> optimizedMessages = new ArrayList<>();
        for (int i = 0; i < messages.size(); i++) {
            Map<String, String> currentMsg = messages.get(i);
            // 检查是否应该合并当前消息
            if (i > 0 && 
                "user".equals(currentMsg.get("role")) && 
                "user".equals(messages.get(i - 1).get("role"))) {
                // 找到上一个添加的相同角色消息并合并内容
                Map<String, String> lastMsg = optimizedMessages.get(optimizedMessages.size() - 1);
                lastMsg.put("content", lastMsg.get("content") + "\n" + currentMsg.get("content"));
            } else {
                optimizedMessages.add(new HashMap<>(currentMsg));
            }
        }
        
        // 检查最后添加的历史消息是否已经包含了当前消息内容
        boolean shouldAddCurrentMessage = true;
        if (!optimizedMessages.isEmpty()) {
            Map<String, String> lastMsg = optimizedMessages.get(optimizedMessages.size() - 1);
            if ("user".equals(lastMsg.get("role")) && content.equals(lastMsg.get("content"))) {
                shouldAddCurrentMessage = false;
                log.debug("当前消息已存在于历史记录中，不重复添加");
            }
        }
        
        // 添加最新的用户消息，同样限制长度
        if (shouldAddCurrentMessage) {
            Map<String, String> latestUserMessage = new HashMap<>();
            latestUserMessage.put("role", "user");
            
            // 压缩用户输入，控制token数量
            if (content != null && content.length() > 500) {
                content = content.substring(0, 500);
                log.debug("用户输入过长，已截断至500字符");
            }
            
            latestUserMessage.put("content", content);
            optimizedMessages.add(latestUserMessage);
        }

        // 6. 调用AI接口获取回复
        String aiResponse = "抱歉，我遇到了一些问题，无法回答您的问题。请稍后再试。"; // 默认初始化值
        try {
            DashScopeClient client = new DashScopeClient(apiKey);
            
            // 添加重试机制，最多重试3次
            int maxRetries = 2; // 减少重试次数，避免过多请求
            int retryCount = 0;
            boolean success = false;
            
            while (!success && retryCount < maxRetries) {
                try {
                    if (retryCount > 0) {
                        // 如果是重试，则等待一段时间（指数退避策略）
                        long waitTime = (long) (2000 * Math.pow(2, retryCount));
                        log.info("等待 {} 毫秒后重试API调用，当前是第 {} 次重试", waitTime, retryCount);
                        Thread.sleep(waitTime);
                    }
                    
                    aiResponse = client.chatCompletion(model, optimizedMessages);
                    log.info("AI响应: {}", aiResponse);
                    
                    // 如果响应中包含"rate limit"或"速率限制"，说明仍然受到限制
                    if (aiResponse.contains("rate limit") || 
                        aiResponse.contains("速率限制") || 
                        aiResponse.contains("API调用失败") || 
                        aiResponse.contains("请求量较大")) {
                        retryCount++;
                        continue;
                    }
                    
                    success = true;
                } catch (Exception e) {
                    log.error("重试过程中发生错误", e);
                    retryCount++;
                    
                    if (retryCount >= maxRetries) {
                        throw e; // 如果已达到最大重试次数，则抛出异常
                    }
                }
            }
            
            if (!success) {
                aiResponse = "非常抱歉，我们的服务暂时遇到了流量限制。请稍后再试，或者尝试简化您的问题。";
            }
        } catch (Exception e) {
            log.error("调用AI接口失败", e);
            aiResponse = "抱歉，我遇到了一些问题，无法回答您的问题。请稍后再试。";
        }

        // 7. 保存AI回复 - 确保isUser字段为字符串"0"
        AiMessage aiMessage = new AiMessage();
        aiMessage.setChatId(chatId);
        aiMessage.setUserId(userId);
        aiMessage.setContent(aiResponse);
        aiMessage.setIsUser("0"); // 0表示AI消息，确保使用字符串
        aiMessage.setMessageType("text");
        aiMessage.setDelFlag("0");
        aiMessageMapper.insertAiMessage(aiMessage);

        return aiMessage;
    }

    /**
     * 分析情绪量表结果
     * 
     * @param questions 问题列表
     * @param answers 回答列表
     * @param scaleId 量表ID
     * @param totalScore 总分
     * @return AI分析结果
     */
    @Override
    public String analyzeMoodScaleResult(List<String> questions, List<String> answers, Long scaleId, Integer totalScore) {
        if (questions == null || answers == null || questions.size() != answers.size()) {
            log.error("问题和答案数量不匹配或为空");
            return "无法分析结果，数据不完整";
        }
        
        try {
            // 构建提示信息
            StringBuilder prompt = new StringBuilder();
            prompt.append("这是一个情绪量表测试，总分为").append(totalScore).append("分。请分析测试结果并给出专业建议。\n\n");
            prompt.append("量表选项说明：在该量表中，选项通常为1=很少，2=有时，3=经常，4=持续。\n\n");
            prompt.append("测试问题和答案如下：\n");
            
            for (int i = 0; i < questions.size(); i++) {
                prompt.append("问题").append(i + 1).append("：").append(questions.get(i)).append("\n");
                prompt.append("回答：").append(answers.get(i)).append("\n");
            }
            
            prompt.append("\n请根据以上情况进行全面分析，包括：\n");
            prompt.append("1. 用户当前可能的情绪状态\n");
            prompt.append("2. 可能存在的心理问题或风险评估\n");
            prompt.append("3. 针对性的改善建议\n");
            prompt.append("4. 是否需要专业心理咨询的建议\n");
            prompt.append("\n请用专业、温和的语气回复，避免过度解读。");
            
            // 创建消息列表
            List<Map<String, String>> messages = new ArrayList<>();
            
            // 添加系统角色消息
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一名专业的心理咨询师，擅长分析情绪量表结果并给出专业建议。回答应当专业、温和、有建设性，避免使用过于专业的术语。在分析时，请特别注意用户选择的答案（很少、有时、经常、持续），这些选项反映了用户情绪状态的频率和强度。");
            messages.add(systemMessage);
            
            // 添加用户消息
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt.toString());
            messages.add(userMessage);
            
            // 调用AI接口
            DashScopeClient client = new DashScopeClient(apiKey);
            String aiResponse = client.chatCompletion(model, messages);
            
            // 记录分析结果
            log.info("情绪量表（ID: {}）分析完成，总分：{}", scaleId, totalScore);
            
            return aiResponse;
            
        } catch (Exception e) {
            log.error("分析情绪量表结果时发生错误", e);
            return "很抱歉，在分析结果时遇到了问题。建议您咨询专业的心理健康顾问以获取更准确的评估。";
        }
    }
} 