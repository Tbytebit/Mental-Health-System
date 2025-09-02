import request from '@/utils/request'
import config from '@/config'

// DashScope API配置
const dashScopeConfig = {
  baseUrl: "https://dashscope.aliyuncs.com/compatible-mode/v1",
  apiKey: config.aiService?.dashScope?.apiKey || "个人API",
  model: config.aiService?.dashScope?.model || "qwen-turbo"
}


export function directChatWithAI(messages) {
  return request({
    baseUrl: dashScopeConfig.baseUrl,
    url: '/chat/completions',
    method: 'post',
    header: {
      'Authorization': 'Bearer ' + dashScopeConfig.apiKey,
      'Content-Type': 'application/json'
    },
    data: {
      model: dashScopeConfig.model,
      messages: messages,
      temperature: 0.7,
      max_tokens: 2000
    }
  })
}

/**
 * 通过后端调用AI聊天（推荐方式）
 * 使用后端代理调用DashScope API，保护API密钥
 */
export function chatWithAI(messages) {
  // 默认使用后端代理方式
  if (!config.aiService?.directCall) {
    return request({
      url: '/ai/dashscope/chat',
      method: 'post',
      data: {
        messages: messages,
        temperature: 0.7,
        max_tokens: 2000
      }
    })
  } else {
    // 如果配置了直接调用模式，则直接调用API（不推荐生产环境使用）
    console.warn('使用直接调用DashScope API模式，生产环境不推荐')
    return directChatWithAI(messages)
  }
}

/**
 * 获取AI助手的系统提示词
 */
export function getAISystemPrompt(assistantType) {
  switch (assistantType) {
    case 0: // 心理顾问
      return "你是一名专业的心理健康顾问，擅长提供情绪管理和心理健康建议。你的回答应该温暖、专业，并基于心理学知识。避免给出医疗诊断，而是提供支持和建议。";
    case 1: // 冥想助手
      return "你是一名冥想指导师，擅长引导用户进行冥想和放松练习。你的回答应该平静、舒缓，帮助用户减轻压力和焦虑。提供简单易行的冥想技巧和呼吸练习。";
    case 2: // 情绪分析师
      return "你是一名情绪分析专家，擅长帮助用户识别、理解和管理自己的情绪。你的回答应该客观、分析性强，帮助用户深入了解自己的情感反应和模式。";
    case 3: // 生活教练
      return "你是一名生活教练，擅长帮助用户设定目标、培养健康习惯和提高生活质量。你的回答应该积极、实用，提供具体的行动建议和激励。";
    default:
      return "你是一名AI助手，致力于提供有用、准确、安全的回答。";
  }
}

/**
 * 格式化消息历史为DashScope API所需格式
 */
export function formatMessagesForAPI(systemPrompt, messageHistory, currentMessage) {
  const formattedMessages = [];
  
  // 添加系统提示词
  if (systemPrompt) {
    formattedMessages.push({
      role: 'system',
      content: systemPrompt
    });
  }
  
  // 添加历史消息（最多10条）
  const historyLimit = 10;
  const startIndex = messageHistory.length > historyLimit ? 
                    messageHistory.length - historyLimit : 0;
  
  for (let i = startIndex; i < messageHistory.length; i++) {
    const message = messageHistory[i];
    formattedMessages.push({
      role: message.isSelf ? 'user' : 'assistant',
      content: message.content
    });
  }
  
  // 添加当前用户消息
  if (currentMessage) {
    formattedMessages.push({
      role: 'user',
      content: currentMessage
    });
  }
  
  return formattedMessages;
}