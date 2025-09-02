package com.doctor.chat.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.doctor.chat.dto.ChatMessageDTO;
import com.doctor.chat.dto.WebSocketMessage;
import com.doctor.chat.entity.ChatMessage;
import com.doctor.chat.entity.ChatUser;
import com.doctor.chat.service.ChatMessageService;
import com.doctor.chat.service.ChatUserService;
import com.doctor.chat.service.FriendRelationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

/**
 * WebSocket服务端
 */
@Slf4j
@Component
@ServerEndpoint("/ws/chat/{userId}")
public class WebSocketServer {
    
    /**
     * 用户ID和WebSocket的映射关系
     */
    private static final Map<String, Session> SESSION_MAP = new ConcurrentHashMap<>();
    
    /**
     * 用户ID与最后活动时间映射（本地缓存）
     */
    private static final Map<String, Long> USER_LAST_ACTIVE_MAP = new ConcurrentHashMap<>();
    
    /**
     * Redis中用户在线状态的key前缀
     */
    private static final String REDIS_ONLINE_KEY_PREFIX = "chat:user:online:";
    
    /**
     * Redis中用户心跳时间的key前缀
     */
    private static final String REDIS_HEARTBEAT_KEY_PREFIX = "chat:user:heartbeat:";
    
    /**
     * 心跳超时时间（毫秒）
     */
    private static final long HEARTBEAT_TIMEOUT = 30 * 1000; // 30秒
    
    /**
     * 好友状态变化通知类型
     */
    private static final int STATUS_CHANGE_MESSAGE_TYPE = 8;
    
    /**
     * 存储所有连接的WebSocket对象
     */
    private static final CopyOnWriteArraySet<WebSocketServer> WEB_SOCKET_SET = new CopyOnWriteArraySet<>();
    
    /**
     * 与客户端的连接会话
     */
    private Session session;
    
    /**
     * 当前连接用户ID
     */
    private String userId;
    
    /**
     * 静态注入
     */
    private static ChatMessageService chatMessageService;
    
    private static RedisTemplate<String, Object> redisTemplate;
    
    private static ChatUserService chatUserService;
    
    private static FriendRelationService friendRelationService;
    
    @Autowired
    public void setChatMessageService(ChatMessageService chatMessageService) {
        WebSocketServer.chatMessageService = chatMessageService;
    }
    
    @Autowired
    @Qualifier("chatRedisTemplate")
    public void setRedisTemplate(RedisTemplate<String, Object> redisTemplate) {
        WebSocketServer.redisTemplate = redisTemplate;
    }
    
    @Autowired
    public void setChatUserService(ChatUserService chatUserService) {
        WebSocketServer.chatUserService = chatUserService;
    }
    
    @Autowired
    public void setFriendRelationService(FriendRelationService friendRelationService) {
        WebSocketServer.friendRelationService = friendRelationService;
    }
    
    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) {
        this.session = session;
        this.userId = userId;
        
        // 存储Session
        if (SESSION_MAP.containsKey(userId)) {
            // 已存在则移除
            SESSION_MAP.remove(userId);
            // 加入新的
            SESSION_MAP.put(userId, session);
        } else {
            // 不存在则直接添加
            SESSION_MAP.put(userId, session);
            // 添加到Set中
            WEB_SOCKET_SET.add(this);
        }
        
        // 更新用户活动时间
        updateUserActiveTime(userId);
        
        // 更新用户在线状态
        try {
            Long userIdLong = Long.parseLong(userId);
            
            // 更新用户状态为在线
            chatUserService.updateUserStatus(userIdLong, "1");
            
            // 将用户状态缓存到Redis中
            redisTemplate.opsForValue().set(REDIS_ONLINE_KEY_PREFIX + userId, "1", 2, TimeUnit.MINUTES);
            // 保存用户最后心跳时间到Redis
            redisTemplate.opsForValue().set(REDIS_HEARTBEAT_KEY_PREFIX + userId, System.currentTimeMillis(), 2, TimeUnit.MINUTES);
            
            log.info("用户：{}状态更新为在线", userId);
        } catch (Exception e) {
            log.error("更新用户状态失败", e);
        }
        
        log.info("用户：{}连接成功，当前在线人数：{}", userId, WEB_SOCKET_SET.size());
        
        // 推送离线消息
        pushOfflineMessage(userId);
        
        // 通知好友该用户上线
        notifyFriendsStatusChange(userId, "1");
    }
    
    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose() {
        // 从Set中移除
        WEB_SOCKET_SET.remove(this);
        // 从Map中移除
        SESSION_MAP.remove(userId);
        // 处理用户离线
        handleUserOffline(userId);
        
        // 通知好友该用户离线
        notifyFriendsStatusChange(userId, "0");
        
        log.info("用户：{}断开连接，当前在线人数：{}", userId, WEB_SOCKET_SET.size());
    }
    
    /**
     * 收到客户端消息后调用的方法
     *
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session) {
        log.info("收到用户：{}的消息：{}", userId, message);
        try {
            // 更新用户活动时间
            updateUserActiveTime(userId);
            
            // 解析消息
            WebSocketMessage webSocketMessage = JSON.parseObject(message, WebSocketMessage.class);
            Integer type = webSocketMessage.getType();
            
            // 根据消息类型处理
            switch (type) {
                case 1:
                    // 建立连接消息，已在onOpen处理
                    break;
                case 2:
                    // 心跳消息，更新用户心跳时间并返回pong
                    handleHeartbeatMessage(webSocketMessage);
                    break;
                case 3:
                    // 聊天消息
                    handleChatMessage(webSocketMessage);
                    break;
                case 4:
                    // 已读回执
                    handleReadReceipt(webSocketMessage);
                    break;
                case 5:
                    // 好友请求
                    handleFriendRequest(webSocketMessage);
                    break;
                case 6:
                    // 好友请求响应
                    handleFriendResponse(webSocketMessage);
                    break;
                default:
                    log.warn("未知的消息类型：{}", type);
            }
        } catch (Exception e) {
            log.error("处理消息发生错误", e);
        }
    }
    
    /**
     * 发生错误时调用
     */
    @OnError
    public void onError(Session session, Throwable error) {
        log.error("用户：{}发生错误：{}", userId, error.getMessage());
        // 错误发生时，检查连接状态
        if (session == null || !session.isOpen()) {
            // 连接已关闭，处理用户离线
            handleUserOffline(userId);
        }
    }
    
    /**
     * 处理心跳消息
     */
    private void handleHeartbeatMessage(WebSocketMessage webSocketMessage) {
        try {
            // 解析心跳数据
            JSONObject heartbeatData = JSON.parseObject(webSocketMessage.getData());
            String userId = heartbeatData.getString("userId");
            Long timestamp = heartbeatData.getLong("timestamp");
            
            if (userId == null) {
                userId = this.userId; // 使用连接中的userId
            }
            
            Long userIdLong = Long.parseLong(userId);
            
            // 更新最后活动时间
            updateUserActiveTime(userId);
            
            // 更新Redis中的心跳时间
            redisTemplate.opsForValue().set(REDIS_HEARTBEAT_KEY_PREFIX + userId, 
                    System.currentTimeMillis(), 2, TimeUnit.MINUTES);
            
            // 确保用户为在线状态
            chatUserService.updateUserStatus(userIdLong, "1");
            
            // 返回心跳响应
            WebSocketMessage responseMessage = new WebSocketMessage();
            responseMessage.setType(2);
            responseMessage.setData(timestamp.toString());
            sendMessage(JSON.toJSONString(responseMessage));
        } catch (Exception e) {
            log.error("处理心跳消息失败", e);
        }
    }
    
    /**
     * 处理聊天消息
     */
    private void handleChatMessage(WebSocketMessage webSocketMessage) {
        try {
            log.info("[handleChatMessage] 开始处理聊天消息，数据: {}", webSocketMessage.getData());
            // 解析消息数据
            JSONObject jsonObject = JSON.parseObject(webSocketMessage.getData());
            
            // 构建消息对象
            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setSenderId(Long.parseLong(userId));
            chatMessage.setReceiverId(jsonObject.getLong("receiverId"));
            chatMessage.setContent(jsonObject.getString("content"));
            chatMessage.setType(jsonObject.getInteger("type"));
            chatMessage.setStatus(0);
            chatMessage.setCreateTime(new Date());
            chatMessage.setUpdateTime(new Date());
            
            // 设置会话ID
            if (jsonObject.containsKey("conversationId")) {
                chatMessage.setConversationId(jsonObject.getString("conversationId"));
            } else {
                // 生成会话ID（较小的用户ID在前）
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
            
            // 设置额外数据（如媒体文件URL等）
            if (jsonObject.containsKey("extraData")) {
                chatMessage.setExtraData(jsonObject.getString("extraData"));
            }
            
            log.info("[handleChatMessage] 准备保存消息到数据库，消息内容: {}", chatMessage);
            // 保存消息到数据库，使用 sendMessage 方法代替 save
            boolean result = chatMessageService.sendMessage(chatMessage);
            
            if (result) {
                log.info("[handleChatMessage] 消息保存成功，准备发送给接收者: {}", chatMessage.getReceiverId());
                // 消息保存成功，推送给接收者
                String receiverId = chatMessage.getReceiverId().toString();
                
                // 构建接收者接收的消息DTO
                ChatMessageDTO messageDTO = new ChatMessageDTO();
                messageDTO.setMessageId(chatMessage.getMessageId());
                messageDTO.setSenderId(chatMessage.getSenderId());
                messageDTO.setReceiverId(chatMessage.getReceiverId());
                messageDTO.setContent(chatMessage.getContent());
                messageDTO.setType(chatMessage.getType());
                messageDTO.setStatus(chatMessage.getStatus());
                messageDTO.setCreateTime(chatMessage.getCreateTime());
                messageDTO.setExtraData(chatMessage.getExtraData());
                messageDTO.setConversationId(chatMessage.getConversationId());
                
                // 检查接收者是否在线
                String receiverStatus = chatUserService.getUserStatus(chatMessage.getReceiverId());
                
                // 包装为WebSocket消息
                WebSocketMessage responseMessage = new WebSocketMessage();
                responseMessage.setType(3);
                responseMessage.setData(JSON.toJSONString(messageDTO));
                
                // 推送消息
                sendMessageToUser(receiverId, JSON.toJSONString(responseMessage));
            } else {
                log.error("[handleChatMessage] 消息保存失败");
            }
        } catch (Exception e) {
            log.error("[handleChatMessage] 处理聊天消息发生错误", e);
        }
    }
    
    /**
     * 处理已读回执
     */
    private void handleReadReceipt(WebSocketMessage webSocketMessage) {
        try {
            // 解析消息数据
            String dataStr = webSocketMessage.getData();
            if (dataStr == null || dataStr.isEmpty()) {
                log.warn("已读回执数据为空");
                return;
            }
            
            JSONObject jsonObject = JSON.parseObject(dataStr);
            if (jsonObject == null) {
                log.warn("已读回执数据格式错误");
                return;
            }
            
            // 获取参数
            Long senderId = null;
            Long receiverId = null;
            
            try {
                // 支持多种类型转换
                Object senderIdObj = jsonObject.get("senderId");
                Object receiverIdObj = jsonObject.get("receiverId");
                
                if (senderIdObj != null) {
                    senderId = Long.valueOf(senderIdObj.toString());
                }
                
                if (receiverIdObj != null) {
                    receiverId = Long.valueOf(receiverIdObj.toString());
                } else {
                    // 如果未提供receiverId，使用当前连接的用户ID
                    receiverId = Long.parseLong(userId);
                }
            } catch (NumberFormatException e) {
                log.error("已读回执参数格式错误", e);
                return;
            }
            
            // 验证参数
            if (senderId == null || receiverId == null) {
                log.warn("已读回执参数不完整: senderId={}, receiverId={}", senderId, receiverId);
                return;
            }
            
            // 标记为已读
            boolean result = chatMessageService.markMessageRead(senderId, receiverId);
            if (!result) {
                log.warn("标记消息已读失败: senderId={}, receiverId={}", senderId, receiverId);
                return;
            }
            
            log.info("消息已标记为已读: senderId={}, receiverId={}", senderId, receiverId);
            
            // 通知发送者消息已读
            WebSocketMessage responseMessage = new WebSocketMessage();
            responseMessage.setType(4);
            JSONObject data = new JSONObject();
            data.put("senderId", senderId);
            data.put("receiverId", receiverId);
            responseMessage.setData(data.toJSONString());
            
            // 推送消息给发送者
            sendMessageToUser(senderId.toString(), JSON.toJSONString(responseMessage));
        } catch (Exception e) {
            log.error("处理已读回执发生错误", e);
        }
    }
    
    /**
     * 处理好友请求
     */
    private void handleFriendRequest(WebSocketMessage webSocketMessage) {
        // 这里可以处理好友请求，但实际上好友请求可以通过HTTP接口处理
        // 这里主要是通知在线用户收到了新的好友请求
        try {
            // 解析消息数据
            JSONObject jsonObject = JSON.parseObject(webSocketMessage.getData());
            Long toUserId = jsonObject.getLong("toUserId");
            
            // 推送好友请求通知给接收者
            WebSocketMessage responseMessage = new WebSocketMessage();
            responseMessage.setType(5);
            responseMessage.setData(webSocketMessage.getData());
            
            sendMessageToUser(toUserId.toString(), JSON.toJSONString(responseMessage));
        } catch (Exception e) {
            log.error("处理好友请求发生错误", e);
        }
    }
    
    /**
     * 处理好友请求响应
     */
    private void handleFriendResponse(WebSocketMessage webSocketMessage) {
        // 这里可以处理好友请求响应，但实际上好友请求响应可以通过HTTP接口处理
        // 这里主要是通知在线用户收到了好友请求的处理结果
        try {
            // 解析消息数据
            JSONObject jsonObject = JSON.parseObject(webSocketMessage.getData());
            Long fromUserId = jsonObject.getLong("fromUserId");
            
            // 推送好友请求响应通知给申请者
            WebSocketMessage responseMessage = new WebSocketMessage();
            responseMessage.setType(6);
            responseMessage.setData(webSocketMessage.getData());
            
            sendMessageToUser(fromUserId.toString(), JSON.toJSONString(responseMessage));
        } catch (Exception e) {
            log.error("处理好友请求响应发生错误", e);
        }
    }
    
    /**
     * 推送离线消息
     */
    private void pushOfflineMessage(String userId) {
        // TODO: 从数据库中获取离线消息
        // 构建离线消息包
        WebSocketMessage responseMessage = new WebSocketMessage();
        responseMessage.setType(7);
        // 这里应该从数据库中查询离线消息，然后推送给用户
        // responseMessage.setData(...);
        
        // 发送离线消息
        sendMessage(JSON.toJSONString(responseMessage));
    }
    
    /**
     * 发送消息
     */
    private void sendMessage(String message) {
        try {
            this.session.getBasicRemote().sendText(message);
        } catch (IOException e) {
            log.error("发送消息发生错误", e);
        }
    }
    
    /**
     * 给指定用户发送消息
     *
     * @param userId  用户ID
     * @param message 消息
     */
    public static void sendMessageToUser(String userId, String message) {
        Session session = SESSION_MAP.get(userId);
        if (session != null && session.isOpen()) {
            try {
                session.getBasicRemote().sendText(message);
            } catch (IOException e) {
                log.error("发送消息给用户：{}发生错误", userId, e);
            }
        } else {
            log.debug("用户：{}不在线，消息将存储为离线消息", userId);
            // 可以在这里保存离线消息
        }
    }
    
    /**
     * 广播消息
     *
     * @param message 消息
     */
    public static void broadcastMessage(String message) {
        for (WebSocketServer webSocketServer : WEB_SOCKET_SET) {
            try {
                webSocketServer.sendMessage(message);
            } catch (Exception e) {
                log.error("广播消息发生错误", e);
            }
        }
    }
    
    /**
     * 更新用户活动时间
     *
     * @param userId 用户ID
     */
    private void updateUserActiveTime(String userId) {
        long currentTime = System.currentTimeMillis();
        // 更新本地缓存
        USER_LAST_ACTIVE_MAP.put(userId, currentTime);
        // 更新Redis缓存
        redisTemplate.opsForValue().set(REDIS_ONLINE_KEY_PREFIX + userId, "1", 2, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set(REDIS_HEARTBEAT_KEY_PREFIX + userId, currentTime, 2, TimeUnit.MINUTES);
    }
    
    /**
     * 处理用户离线
     *
     * @param userId 用户ID
     */
    private void handleUserOffline(String userId) {
        try {
            Long userIdLong = Long.parseLong(userId);
            
            // 更新用户状态为离线
            chatUserService.updateUserStatus(userIdLong, "0");
            
            // 更新Redis中的状态
            redisTemplate.opsForValue().set(REDIS_ONLINE_KEY_PREFIX + userId, "0", 2, TimeUnit.MINUTES);
            
            log.info("用户：{}状态更新为离线", userId);
        } catch (Exception e) {
            log.error("更新用户离线状态失败", e);
        }
    }
    
    /**
     * 通知好友状态变化
     */
    private void notifyFriendsStatusChange(String userId, String status) {
        try {
            // 获取该用户的好友列表
            Long userIdLong = Long.parseLong(userId);
            List<Map<String, Object>> friendList = friendRelationService.getFriendList(userIdLong);
            
            // 构建状态变化消息
            WebSocketMessage statusMessage = new WebSocketMessage();
            statusMessage.setType(STATUS_CHANGE_MESSAGE_TYPE);
            
            Map<String, Object> statusData = new HashMap<>();
            statusData.put("userId", userId);
            statusData.put("status", status);
            statusData.put("timestamp", System.currentTimeMillis());
            
            statusMessage.setData(JSON.toJSONString(statusData));
            String messageStr = JSON.toJSONString(statusMessage);
            
            // 向所有在线好友发送状态变化通知
            for (Map<String, Object> friend : friendList) {
                Object friendIdObj = friend.get("friendId");
                if (friendIdObj != null) {
                    String friendId = friendIdObj.toString();
                    sendMessageToUser(friendId, messageStr);
                }
            }
            
            log.info("已通知{}个好友用户{}状态变化为{}", friendList.size(), userId, status);
        } catch (Exception e) {
            log.error("通知好友状态变化失败", e);
        }
    }
    
    /**
     * 检查心跳超时的用户
     */
    @Scheduled(fixedRate = 15000) // 每15秒执行一次，提高检测频率
    public static void checkHeartbeatTimeout() {
        try {
            // 调用服务清理超时用户
            int count = chatUserService.cleanTimeoutUsers(HEARTBEAT_TIMEOUT);
            
            if (count > 0) {
                log.info("已清理{}个超时未心跳的用户", count);
            }
        } catch (Exception e) {
            log.error("检查心跳超时失败", e);
        }
    }
    
    /**
     * 定时从Redis同步用户状态
     * 每分钟执行一次
     */
    @Scheduled(fixedRate = 60000)
    public void syncUserStatusFromRedis() {
        log.debug("开始从Redis同步用户状态...");
        
        try {
            // 获取所有在线用户的key
            Set<String> keys = redisTemplate.keys(REDIS_ONLINE_KEY_PREFIX + "*");
            if (keys != null && !keys.isEmpty()) {
                for (String key : keys) {
                    String userId = key.substring(REDIS_ONLINE_KEY_PREFIX.length());
                    Object status = redisTemplate.opsForValue().get(key);
                    
                    if (status != null) {
                        String statusStr = status.toString();
                        
                        // 获取心跳时间
                        String heartbeatKey = REDIS_HEARTBEAT_KEY_PREFIX + userId;
                        Object heartbeatObj = redisTemplate.opsForValue().get(heartbeatKey);
                        Long heartbeatTime = heartbeatObj != null ? Long.parseLong(heartbeatObj.toString()) : null;
                        
                        // 检查心跳是否超时
                        if (heartbeatTime != null) {
                            long currentTime = System.currentTimeMillis();
                            long timeDiff = currentTime - heartbeatTime;
                            
                            // 如果超过心跳超时时间，将用户标记为离线
                            if (timeDiff > HEARTBEAT_TIMEOUT && "1".equals(statusStr)) {
                                log.info("Redis中用户：{}心跳超时（{}ms），将标记为离线", userId, timeDiff);
                                redisTemplate.opsForValue().set(key, "0", 2, TimeUnit.MINUTES);
                                
                                // 更新数据库中的用户状态
                                try {
                                    Long userIdLong = Long.parseLong(userId);
                                    chatUserService.updateUserStatus(userIdLong, "0");
                                } catch (Exception e) {
                                    log.error("更新用户状态失败", e);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            log.error("从Redis同步用户状态失败", e);
        }
        
        log.debug("从Redis同步用户状态完成");
    }
}
