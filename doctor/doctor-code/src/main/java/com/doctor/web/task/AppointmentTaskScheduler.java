package com.doctor.web.task;

import com.doctor.web.domain.AppointmentTask;
import com.doctor.web.service.IAppointmentTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

/**
 * 预约任务调度器
 * 负责定时检查并执行需要处理的预约任务
 */
@Component
@EnableScheduling
public class AppointmentTaskScheduler {
    
    private static final Logger logger = LoggerFactory.getLogger(AppointmentTaskScheduler.class);
    
    @Autowired
    private IAppointmentTaskService appointmentTaskService;
    
    @Autowired
    private FriendRequestSender friendRequestSender;
    
    /**
     * 每分钟检查一次待处理的预约任务
     * 查找所有已到执行时间但尚未执行的任务
     */
    @Scheduled(cron = "0 * * * * ?")
    public void processPendingTasks() {
        logger.info("开始处理待执行的预约任务...");
        
        try {
            // 查询当前时间应该执行的任务
            List<AppointmentTask> pendingTasks = appointmentTaskService.findPendingTasks(new Date());
            
            if (pendingTasks.isEmpty()) {
                logger.info("没有待处理的预约任务");
                return;
            }
            
            logger.info("找到 {} 个待处理的预约任务", pendingTasks.size());
            
            // 处理每个任务
            for (AppointmentTask task : pendingTasks) {
                try {
                    // 执行发送好友申请
                    boolean success = friendRequestSender.sendFriendRequest(
                        task.getPatientId(), 
                        task.getDoctorId(),
                        task.getDoctorName()
                    );
                    
                    // 更新任务状态
                    if (success) {
                        task.setStatus(1); // 1表示已处理
                        task.setExecuteTime(new Date());
                        appointmentTaskService.updateAppointmentTask(task);
                        logger.info("任务处理成功：任务ID={}, 预约ID={}", task.getTaskId(), task.getAppointmentId());
                    } else {
                        // 如果失败，可以记录失败次数，达到一定次数后标记为失败
                        logger.error("任务处理失败：任务ID={}, 预约ID={}", task.getTaskId(), task.getAppointmentId());
                    }
                } catch (Exception e) {
                    logger.error("处理任务异常：任务ID={}, 错误={}", task.getTaskId(), e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            logger.error("处理预约任务时发生异常", e);
        }
    }
} 