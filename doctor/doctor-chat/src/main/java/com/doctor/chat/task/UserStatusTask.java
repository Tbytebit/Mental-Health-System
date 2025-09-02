package com.doctor.chat.task;

import com.doctor.chat.service.ChatUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 用户状态管理定时任务
 */
@Slf4j
@Component
public class UserStatusTask {

    @Autowired
    private ChatUserService chatUserService;

    /**
     * 每分钟检查一次超时的用户
     * 将60秒内没有发送心跳的用户标记为离线
     */
    @Scheduled(fixedRate = 60000)
    public void checkTimeoutUsers() {
        try {
            // 设置超时时间为60秒
            long timeoutMillis = 60 * 1000;
            
            int count = chatUserService.cleanTimeoutUsers(timeoutMillis);
            
            if (count > 0) {
                log.info("定时任务清理了{}个超时未心跳的用户", count);
            }
        } catch (Exception e) {
            log.error("清理超时用户任务执行失败", e);
        }
    }
} 