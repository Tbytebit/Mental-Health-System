package com.doctor.ai.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.X509EncodedKeySpec;
// import java.util.Base64; // Keep for encryptInputWithAes, but specific calls below will use Apache Commons
import org.apache.commons.codec.binary.Base64; // Added for explicit Base64 operations

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 阿里云通义千问API客户端
 */
public class DashScopeClient {
    private static final String PUBLIC_KEY_ID = "**";
    private static final String PUBLIC_KEY = "****";

    private static final int AES_KEY_SIZE = 128; // AES密钥长度
    private static final int GCM_IV_LENGTH = 12; // 12 字节 IV
    private static final int GCM_TAG_LENGTH = 128; // 128 位 Tag(16 字节)

    private static final Logger log = LoggerFactory.getLogger(DashScopeClient.class);
    
    private static final String API_URL = "https://dashscope.aliyuncs.com/api/v1/services/aigc/text-generation/generation";
    
    // 令牌桶配置
    private static final int BUCKET_CAPACITY = 500; // 令牌桶容量（略小于QPM限制500）
    private static final int TOKEN_REFILL_RATE = 7; // 每秒钟添加的令牌数
    private static int availableTokens = BUCKET_CAPACITY; // 当前可用令牌数
    private static long lastRefillTimestamp = System.currentTimeMillis(); // 上次令牌补充时间
    private static final ReentrantLock tokenBucketLock = new ReentrantLock();
    
    // 单个用户请求速率限制（每分钟最多5次）
    private static final Map<Long, UserRateLimiter> USER_RATE_LIMITERS = new ConcurrentHashMap<>();
    private static final int MAX_USER_REQUESTS_PER_MINUTE = 5;
    
    // 并发请求控制（最多允许10个并发请求）
    private static final Semaphore REQUEST_SEMAPHORE = new Semaphore(10);
    
    // 全局计数器（用于监控TPM）
    private static final AtomicInteger TOKEN_COUNTER = new AtomicInteger(0);
    private static long TOKEN_COUNTER_RESET_TIME = System.currentTimeMillis();
    private static final int MAX_TOKENS_PER_MINUTE = 800000; // 略小于TPM限制800000
    
    // 简单的响应缓存，用于缓存相同问题的回答
    private static final Map<String, CacheEntry> responseCache = new ConcurrentHashMap<>();
    // 缓存过期时间（毫秒）
    private static final long CACHE_EXPIRY_TIME = 24 * 60 * 60 * 1000; // 24小时
    
    private final String apiKey;

    public DashScopeClient(String apiKey) {
        this.apiKey = apiKey;
    }

    private static SecretKey generateAesSecretKey() throws Exception {
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");
        SecureRandom secureRandom = new SecureRandom();
        keyGen.init(AES_KEY_SIZE, secureRandom);
        return keyGen.generateKey();
    }

    private static byte[] generateIv() {
        byte[] iv = new byte[GCM_IV_LENGTH];
        SecureRandom secureRandom = new SecureRandom(); // 随机生成确保唯一性
        secureRandom.nextBytes(iv);
        return iv;
    }

    private static String encryptInputWithAes(SecretKey aesSecretKey, byte[] iv, String input) throws Exception {
        byte[] content = input.getBytes(StandardCharsets.UTF_8);

        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        aesCipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(aesSecretKey.getEncoded(), "AES"), gcmParameterSpec);

        byte[] encryptedBytes = aesCipher.doFinal(content);
        return org.apache.commons.codec.binary.Base64.encodeBase64String(encryptedBytes);
    }

    private static String encryptAesKeyWithRsaPublicKey(SecretKey aesSecretKey, String publicKeyString) throws Exception {
        byte[] aesKeyBytes = aesSecretKey.getEncoded();
        // 1. 将原始AES密钥进行Base64编码
        String base64AesKey = org.apache.commons.codec.binary.Base64.encodeBase64String(aesKeyBytes);

        // 2. 获取RSA公钥
        byte[] publicKeyBytes = org.apache.commons.codec.binary.Base64.decodeBase64(publicKeyString); // 使用 Apache Commons Codec Base64 解码
        X509EncodedKeySpec spec = new X509EncodedKeySpec(publicKeyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey pubKey = kf.generatePublic(spec);

        // 3. 使用RSA公钥加密Base64编码后的AES密钥字符串
        Cipher rsaCipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        rsaCipher.init(Cipher.ENCRYPT_MODE, pubKey);
        // 对Base64编码后的字符串获取字节进行加密
        byte[] encryptedBytes = rsaCipher.doFinal(base64AesKey.getBytes(StandardCharsets.UTF_8)); 
        // 4. 将加密后的字节数组再进行Base64编码作为最终结果
        return org.apache.commons.codec.binary.Base64.encodeBase64String(encryptedBytes);
    }

    private static String decryptOutputWithAes(SecretKey aesSecretKey, byte[] iv, String encryptedContent) throws Exception {
        // 1. 解码Base64编码的加密内容
        byte[] encryptedBytes = org.apache.commons.codec.binary.Base64.decodeBase64(encryptedContent);

        // 2. 使用AES密钥和IV解密内容
        Cipher aesCipher = Cipher.getInstance("AES/GCM/NoPadding");
        GCMParameterSpec gcmParameterSpec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
        aesCipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(aesSecretKey.getEncoded(), "AES"), gcmParameterSpec);

        // 3. 解密并返回原始字符串
        byte[] decryptedBytes = aesCipher.doFinal(encryptedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }


    /**
     * 通过HTTP直接调用通义千问API完成聊天
     *
     * @param model 模型名称，如qwen-turbo, qwen-plus等
     * @param messages 消息列表
     * @param userId 用户ID，用于用户级限流（可选）
     * @return AI回复内容
     * @throws IOException API调用异常
     */
    public String chatCompletion(String model, List<Map<String, String>> messages, Long userId) throws IOException {
        // 生成缓存键
        String cacheKey = generateCacheKey(model, messages);
        
        // 检查缓存中是否有响应
        CacheEntry cachedResponse = responseCache.get(cacheKey);
        if (cachedResponse != null && !cachedResponse.isExpired()) {
            log.debug("使用缓存的响应: {}", cacheKey);
            return cachedResponse.getResponse();
        }
        
        // 如果提供了用户ID，检查用户级限流
        if (userId != null) {
            UserRateLimiter userLimiter = USER_RATE_LIMITERS.computeIfAbsent(userId, id -> new UserRateLimiter());
            if (!userLimiter.tryAcquire()) {
                log.warn("用户 {} 请求频率过高", userId);
                return "您的请求过于频繁，请稍后再试（最多每分钟5次请求）。";
            }
        }
        
        // 尝试获取令牌，控制并发请求数量
        try {
            if (!REQUEST_SEMAPHORE.tryAcquire(30, TimeUnit.SECONDS)) {
                log.warn("无法获取请求令牌，请求队列已满");
                return "系统当前请求较多，请稍后再试。";
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("等待请求令牌时被中断", e);
            return "请求被中断，请稍后再试。";
        }
        
        try {
            // 检查并获取令牌桶中的令牌
            if (!acquireToken()) {
                log.warn("令牌桶中没有足够的令牌，API请求受限");
                return "系统当前请求量较大，已达到速率限制。请稍后再试。";
            }
            
            // 预估token消耗并检查TPM限制
            int estimatedTokens = estimateTokenCount(messages);
            if (!checkTokenLimit(estimatedTokens)) {
                log.warn("已达到每分钟最大token数量限制");
                return "系统当前请求量较大，已达到token限制。请稍后再试。";
            }
            
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(API_URL);
                
                // 设置请求头
                httpPost.setHeader("Authorization", "Bearer " + apiKey);
                httpPost.setHeader("Content-Type", "application/json");
                
                // 构建请求体
                Map<String, Object> requestData = new HashMap<>();
                requestData.put("model", model);

                try {
                    // 1. 生成AES密钥
                    SecretKey aesKey = generateAesSecretKey();
                    // 2. 生成IV
                    byte[] iv = generateIv();
                    
                    // 3. 构建需要加密的敏感数据（不包括messages）
                    Map<String, Object> sensitiveData = new HashMap<>();
                    // 这里可以添加其他需要加密的敏感字段
                    // sensitiveData.put("sensitive_field", "sensitive_value");
                    
                    // 4. 加密敏感数据
                    String sensitiveJson = JSON.toJSONString(sensitiveData);
                    String encryptedContent = encryptInputWithAes(aesKey, iv, sensitiveJson);

                    // 5. 使用RSA公钥加密AES密钥
                    String encryptedAesKey = encryptAesKeyWithRsaPublicKey(aesKey, PUBLIC_KEY);

                    // 6. 构建input结构，messages保留在顶层
                    Map<String, Object> inputMap = new HashMap<>();
                    inputMap.put("messages", messages); // messages字段必须在顶层，不能加密
                    inputMap.put("encrypted_content", encryptedContent);
                    inputMap.put("encrypt_type", "AES/GCM/NoPadding");
                    inputMap.put("iv", org.apache.commons.codec.binary.Base64.encodeBase64String(iv));
                    inputMap.put("public_key_id", PUBLIC_KEY_ID);
                    inputMap.put("encrypted_key", encryptedAesKey);
                    
                    requestData.put("input", inputMap);

                    // 6. 设置parameters
                    Map<String, Object> parameters = new HashMap<>();
                    parameters.put("temperature", 0.7);
                    parameters.put("max_tokens", 800);
                    parameters.put("result_format", "message");
                    requestData.put("parameters", parameters);
                    
                    // 保存AES密钥和IV用于解密响应
                    requestData.put("_aes_key", aesKey); // 临时存储，不会发送到API
                    requestData.put("_iv", iv); // 临时存储，不会发送到API

                } catch (Exception e) {
                    log.error("处理请求数据时出错", e);
                    return "处理请求时发生内部错误，请稍后再试。";
                }
                
                // 提取密钥信息用于解密，然后从请求数据中移除
                SecretKey aesKey = (SecretKey) requestData.remove("_aes_key");
                byte[] iv = (byte[]) requestData.remove("_iv");
                
                String jsonBody = JSON.toJSONString(requestData);
                log.debug("Request body: {}", jsonBody);
                
                httpPost.setEntity(new StringEntity(jsonBody, ContentType.APPLICATION_JSON));
                
                // 发送请求并获取响应
                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    HttpEntity entity = response.getEntity();
                    String responseBody = EntityUtils.toString(entity, "UTF-8");
                    log.debug("Response: {}", responseBody);
                    
                    // 解析响应
                    JSONObject jsonResponse = JSON.parseObject(responseBody);
                    
                    // 检查是否有错误
                    if (jsonResponse.containsKey("code") && !jsonResponse.getString("code").equals("Success")) {
                        String errorMsg = jsonResponse.getString("message");
                        String errorCode = jsonResponse.getString("code");
                        log.error("DashScope API error: {} ({})", errorMsg, errorCode);
                        
                        // 对不同错误类型进行不同处理
                        if ("Throttling.RateQuota".equals(errorCode)) {
                            // 限流错误，减少令牌数量以防止后续请求
                            tokenBucketLock.lock();
                            try {
                                availableTokens = Math.max(0, availableTokens - 50);
                            } finally {
                                tokenBucketLock.unlock();
                            }
                            return "系统当前请求过多，请稍后再试。我们正在优化系统以提供更好的服务。";
                        } else {
                            return "API调用失败: " + errorMsg;
                        }
                    }
                    
                    // 提取AI回复内容
                    try {
                        // 检查响应是否加密
                        if (jsonResponse.getJSONObject("output") != null && jsonResponse.getJSONObject("output").containsKey("encrypted_content")) {
                            // 实现响应解密逻辑
                            try {
                                String encryptedContent = jsonResponse.getJSONObject("output").getString("encrypted_content");
                                String decryptedContent = decryptOutputWithAes(aesKey, iv, encryptedContent);
                                
                                // 解析解密后的内容
                                JSONObject decryptedOutput = JSON.parseObject(decryptedContent);
                                if (decryptedOutput.getJSONArray("choices") != null && 
                                    !decryptedOutput.getJSONArray("choices").isEmpty() &&
                                    decryptedOutput.getJSONArray("choices").getJSONObject(0).getJSONObject("message") != null) {
                                    
                                    String aiResponse = decryptedOutput.getJSONArray("choices")
                                            .getJSONObject(0)
                                            .getJSONObject("message")
                                            .getString("content");
                                    
                                    // 缓存响应
                                    responseCache.put(cacheKey, new CacheEntry(aiResponse));
                                    return aiResponse;
                                } else {
                                    log.error("解密后的响应格式不正确: {}", decryptedContent);
                                    return "抱歉，解密后的响应格式不正确。";
                                }
                            } catch (Exception e) {
                                log.error("解密响应时发生错误", e);
                                return "抱歉，解密响应时发生错误。";
                            }
                        } else if (jsonResponse.getJSONObject("output") != null && 
                                   jsonResponse.getJSONObject("output").getJSONArray("choices") != null && 
                                   !jsonResponse.getJSONObject("output").getJSONArray("choices").isEmpty() &&
                                   jsonResponse.getJSONObject("output").getJSONArray("choices").getJSONObject(0).getJSONObject("message") != null) {
                            // 处理未加密的响应
                            String aiResponse = jsonResponse.getJSONObject("output")
                                    .getJSONArray("choices")
                                    .getJSONObject(0)
                                    .getJSONObject("message")
                                    .getString("content");

                            // 缓存响应
                            responseCache.put(cacheKey, new CacheEntry(aiResponse));
                            return aiResponse;
                        } else {
                            log.error("无法从API响应中提取内容: {}", jsonResponse.toJSONString());
                            return "抱歉，无法从API响应中提取有效内容。";
                        }
                    } catch (Exception e) {
                        log.error("Failed to parse API response", e);
                        return "抱歉，解析API响应时发生错误。";
                    }
                }
            }
        } finally {
            // 释放令牌
            REQUEST_SEMAPHORE.release();
        }
    }
    
    /**
     * 重载方法，不需要用户ID的场景
     */
    public String chatCompletion(String model, List<Map<String, String>> messages) throws IOException {
        return chatCompletion(model, messages, null);
    }
    
    /**
     * 从令牌桶中获取令牌
     * 使用令牌桶算法控制请求速率
     */
    private boolean acquireToken() {
        tokenBucketLock.lock();
        try {
            // 计算需要补充的令牌数量
            long now = System.currentTimeMillis();
            long timePassed = now - lastRefillTimestamp;
            int tokensToAdd = (int) (timePassed / 1000.0 * TOKEN_REFILL_RATE);
            
            if (tokensToAdd > 0) {
                availableTokens = Math.min(BUCKET_CAPACITY, availableTokens + tokensToAdd);
                lastRefillTimestamp = now;
            }
            
            // 如果有足够的令牌，则获取一个令牌
            if (availableTokens > 0) {
                availableTokens--;
                log.debug("获取令牌成功，当前剩余令牌: {}/{}", availableTokens, BUCKET_CAPACITY);
                return true;
            } else {
                log.warn("令牌桶已空，请求被限制");
                return false;
            }
        } finally {
            tokenBucketLock.unlock();
        }
    }
    
    /**
     * 检查并更新TPM计数器
     */
    private boolean checkTokenLimit(int estimatedTokens) {
        long currentTime = System.currentTimeMillis();
        // 检查是否需要重置计数器（每分钟重置一次）
        if (currentTime - TOKEN_COUNTER_RESET_TIME >= 60000) {
            TOKEN_COUNTER.set(0);
            TOKEN_COUNTER_RESET_TIME = currentTime;
            log.debug("重置token计数器");
        }
        
        // 检查是否达到限制
        int currentCount = TOKEN_COUNTER.get();
        if (currentCount + estimatedTokens > MAX_TOKENS_PER_MINUTE) {
            log.warn("达到每分钟最大token数量 {} 限制，当前: {}, 请求: {}", 
                    MAX_TOKENS_PER_MINUTE, currentCount, estimatedTokens);
            return false;
        }
        
        // 增加计数器
        TOKEN_COUNTER.addAndGet(estimatedTokens);
        log.debug("当前分钟的token数: {}/{}", TOKEN_COUNTER.get(), MAX_TOKENS_PER_MINUTE);
        return true;
    }
    
    /**
     * 估算请求消耗的token数量
     * 简单实现，实际情况下可能需要更复杂的算法
     */
    private int estimateTokenCount(List<Map<String, String>> messages) {
        int estimatedTokens = 0;
        for (Map<String, String> message : messages) {
            String content = message.get("content");
            if (content != null) {
                // 粗略估计：中文每字约2个token，英文每4个字符约1个token
                // 这里简化为每个字符1.5个token
                estimatedTokens += content.length() * 1.5;
            }
        }
        // 回复估计值
        estimatedTokens += 800; // 假设回复最大800 tokens
        return estimatedTokens;
    }
    
    /**
     * 生成缓存键
     */
    private String generateCacheKey(String model, List<Map<String, String>> messages) {
        StringBuilder sb = new StringBuilder();
        sb.append(model).append(":");
        
        // 只使用最后一条用户消息作为缓存键的一部分
        if (!messages.isEmpty()) {
            Map<String, String> lastMessage = messages.get(messages.size() - 1);
            if ("user".equals(lastMessage.get("role"))) {
                sb.append(lastMessage.get("content"));
            }
        }
        
        return sb.toString().hashCode() + "";
    }
    
    /**
     * 缓存条目，包含响应和过期时间
     */
    private static class CacheEntry {
        private final String response;
        private final long expiryTime;
        
        public CacheEntry(String response) {
            this.response = response;
            this.expiryTime = System.currentTimeMillis() + CACHE_EXPIRY_TIME;
        }
        
        public String getResponse() {
            return response;
        }
        
        public boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
    
    /**
     * 用户级限流器，限制单个用户的请求频率
     */
    private static class UserRateLimiter {
        private final AtomicInteger requestCount = new AtomicInteger(0);
        private long resetTime = System.currentTimeMillis();
        private final ReentrantLock lock = new ReentrantLock();
        
        public boolean tryAcquire() {
            lock.lock();
            try {
                long now = System.currentTimeMillis();
                // 检查是否需要重置计数器
                if (now - resetTime >= 60000) {
                    requestCount.set(0);
                    resetTime = now;
                }
                
                // 检查是否达到用户限制
                if (requestCount.get() >= MAX_USER_REQUESTS_PER_MINUTE) {
                    return false;
                }
                
                // 增加计数
                requestCount.incrementAndGet();
                return true;
            } finally {
                lock.unlock();
            }
        }
    }
}