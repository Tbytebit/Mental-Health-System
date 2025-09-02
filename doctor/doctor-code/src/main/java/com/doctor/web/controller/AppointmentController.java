package com.doctor.web.controller;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.servlet.http.HttpServletResponse;
import java.util.Calendar;

import com.doctor.common.core.domain.R;
import com.doctor.common.utils.DateUtils;
import com.doctor.common.utils.SecurityUtils;
import com.doctor.web.domain.AppointmentTask;
import com.doctor.web.domain.DoctorScheduled;
import com.doctor.web.service.IAppointmentTaskService;
import com.doctor.web.service.IDoctorScheduledService;
import com.doctor.web.task.FriendRequestRemover;
import eu.bitwalker.useragentutils.browser.SafariUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.doctor.common.annotation.Log;
import com.doctor.common.core.controller.BaseController;
import com.doctor.common.core.domain.AjaxResult;
import com.doctor.common.enums.BusinessType;
import com.doctor.web.domain.Appointment;
import com.doctor.web.service.IAppointmentService;
import com.doctor.common.utils.poi.ExcelUtil;
import com.doctor.common.core.page.TableDataInfo;

/**
 * 预约记录Controller
 *
 * @author Li
 * @date 2024-05-15
 */
@RestController
@RequestMapping("/maincode/appointment")
public class AppointmentController extends BaseController {
    
    private static final Logger logger = LoggerFactory.getLogger(AppointmentController.class);
    
    @Autowired
    private IAppointmentService appointmentService;
    
    @Autowired
    private IDoctorScheduledService doctorScheduledService;
    
    @Autowired
    private IAppointmentTaskService appointmentTaskService;
    
    @Autowired
    private FriendRequestRemover friendRequestRemover;
    
    private final Lock lock = new ReentrantLock();

    /**
     * 查询预约记录列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:appointment:list')")
    @GetMapping("/list")
    public TableDataInfo list(Appointment appointment) {
        if (SecurityUtils.getUserId() != 1L){
            appointment.setDoctorId(SecurityUtils.getUserId());
        }
        startPage();
        List<Appointment> list = appointmentService.selectAppointmentList(appointment);
        return getDataTable(list);
    }

    /**
     * 导出预约记录列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:appointment:export')")
    @Log(title = "预约记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Appointment appointment) {
        List<Appointment> list = appointmentService.selectAppointmentList(appointment);
        ExcelUtil<Appointment> util = new ExcelUtil<Appointment>(Appointment.class);
        util.exportExcel(response, list, "预约记录数据");
    }

    /**
     * 获取预约记录详细信息
     */
    @PreAuthorize("@ss.hasPermi('maincode:appointment:query')")
    @GetMapping(value = "/{appointmentId}")
    public AjaxResult getInfo(@PathVariable("appointmentId") Long appointmentId) {
        return success(appointmentService.selectAppointmentByAppointmentId(appointmentId));
    }

    /**
     * 新增预约记录
     */
    @PreAuthorize("@ss.hasPermi('maincode:appointment:add')")
    @Log(title = "预约记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Appointment appointment) {
        return toAjax(appointmentService.insertAppointment(appointment));
    }

    /**
     * 修改预约记录
     */
    @PreAuthorize("@ss.hasPermi('maincode:appointment:edit')")
    @Log(title = "预约记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Appointment appointment) {
        return toAjax(appointmentService.updateAppointment(appointment));
    }

    /**
     * 删除预约记录
     */
    @PreAuthorize("@ss.hasPermi('maincode:appointment:remove')")
    @Log(title = "预约记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{appointmentIds}")
    public AjaxResult remove(@PathVariable Long[] appointmentIds) {
        return toAjax(appointmentService.deleteAppointmentByAppointmentIds(appointmentIds));
    }

    /**
     * 新增预约记录（用户端）
     * 创建预约的同时，创建一个定时任务，在预约时间到达时自动发送好友申请
     */
    @Log(title = "用户预约", businessType = BusinessType.INSERT)
    @PostMapping("/userAppointment")
    @Transactional(rollbackFor = Exception.class)
    public R userAppointment(@RequestBody Appointment appointment) {
        lock.lock(); // 获取锁
        try {
            // 获取医生排班信息
            DoctorScheduled doctorScheduled = doctorScheduledService.selectDoctorScheduledById(appointment.getDoctorScheduledId());
            if (doctorScheduled.getRemainder() < 1) {
                return R.fail("没有号了");
            }
            
            // 设置预约信息
            appointment.setCreateTime(new Date());
            appointment.setCreateBy(SecurityUtils.getUserId().toString());
            appointment.setPatientId(SecurityUtils.getUserId());
            appointment.setAppointmentStatus(1); // 1表示已预约
            Random random = new Random();
            appointment.setCode((random.nextInt(900000) + 100000) + ""); // 生成一个六位数的随机数字
            
            // 插入预约记录
            int insertAppointment = appointmentService.insertAppointment(appointment);
            
            if (insertAppointment > 0) {
                // 更新医生排班余号数量
                doctorScheduled.setRemainder(doctorScheduled.getRemainder() - 1);
                doctorScheduledService.updateDoctorScheduled(doctorScheduled);
                
                // 创建定时任务，用于预约时间到达时自动发送好友申请
                try {
                    // 解析预约时间
                    Date appointmentDateTime = null;
                    try {
                        // 检查预约日期是否为空
                        if (appointment.getAppointmentDate() == null) {
                            logger.error("预约日期为空，无法创建定时任务");
                            throw new IllegalArgumentException("预约日期不能为空");
                        }
                        
                        // 检查预约时段是否为空
                        if (appointment.getAppointmentTime() == null || appointment.getAppointmentTime().trim().isEmpty()) {
                            logger.error("预约时段为空，无法创建定时任务");
                            throw new IllegalArgumentException("预约时段不能为空");
                        }
                        
                        // 根据医生排班信息计算实际预约时间
                        // 直接使用医生排班的startTime作为预约时间
                        if (doctorScheduled.getStartTime() != null) {
                            appointmentDateTime = doctorScheduled.getStartTime();
                            logger.info("使用医生排班开始时间: {}", appointmentDateTime);
                        } else {
                            // 如果没有开始时间，则尝试解析appointmentDate和appointmentTime
                            logger.info("开始尝试解析预约时间，日期: {}, 时段: {}", 
                                appointment.getAppointmentDate(), appointment.getAppointmentTime());
                            
                            Date appointmentDate;
                            try {
                                appointmentDate = DateUtils.parseDate(appointment.getAppointmentDate());
                                if (appointmentDate == null) {
                                    logger.error("解析预约日期失败，结果为null");
                                    throw new IllegalArgumentException("无法解析预约日期");
                                }
                            } catch (Exception e) {
                                logger.error("解析预约日期出错: {}", e.getMessage());
                                throw new IllegalArgumentException("预约日期格式不正确");
                            }
                            
                            String timeSlot = appointment.getAppointmentTime(); // 例如: "上午 8:00-12:00"
                            
                            // 提取开始时间，假设格式为"上午 8:00-12:00"或"下午 14:00-17:00"等
                            String[] parts = timeSlot.split(" ");
                            if (parts.length < 2) {
                                logger.error("预约时段格式不正确: {}", timeSlot);
                                throw new IllegalArgumentException("预约时段格式不正确");
                            }
                            
                            String[] timeParts = parts[1].split("-");
                            if (timeParts.length < 2) {
                                logger.error("时间范围格式不正确: {}", parts[1]);
                                throw new IllegalArgumentException("时间范围格式不正确");
                            }
                            
                            String startTime = timeParts[0]; // 例如: "8:00"
                            
                            // 尝试获取日期字符串
                            String dateStr;
                            try {
                                dateStr = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD, appointmentDate);
                                if (dateStr == null || dateStr.trim().isEmpty()) {
                                    logger.error("日期格式化失败，结果为null或空字符串");
                                    throw new IllegalArgumentException("日期格式化失败");
                                }
                            } catch (Exception e) {
                                logger.error("日期格式化出错: {}", e.getMessage());
                                throw new IllegalArgumentException("日期格式化失败");
                            }
                            
                            // 结合日期和时间
                            String fullDateTime = dateStr + " " + startTime + ":00";
                            logger.info("解析预约时间，完整时间字符串: {}", fullDateTime);
                            
                            try {
                                appointmentDateTime = DateUtils.parseDate(fullDateTime, DateUtils.YYYY_MM_DD_HH_MM_SS);
                                if (appointmentDateTime == null) {
                                    logger.error("解析完整时间失败，结果为null");
                                    throw new IllegalArgumentException("无法解析完整时间");
                                }
                            } catch (Exception e) {
                                logger.error("解析完整时间出错: {}", e.getMessage());
                                throw new IllegalArgumentException("完整时间格式不正确");
                            }
                        }
                    } catch (Exception e) {
                        logger.error("解析预约时间失败: {}", e.getMessage(), e);
                        // 如果解析失败，使用当前时间加一小时作为默认预约时间
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.HOUR_OF_DAY, 1);
                        appointmentDateTime = calendar.getTime();
                        logger.info("使用默认预约时间: {}", appointmentDateTime);
                    }
                    
                    // 确保appointmentDateTime不为null
                    if (appointmentDateTime == null) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.HOUR_OF_DAY, 1);
                        appointmentDateTime = calendar.getTime();
                        logger.warn("appointmentDateTime仍为null，使用当前时间+1小时: {}", appointmentDateTime);
                    }
                    
                    // 创建预约任务
                    AppointmentTask task = new AppointmentTask();
                    task.setAppointmentId(appointment.getAppointmentId());
                    task.setPatientId(appointment.getPatientId());
                    task.setDoctorId(appointment.getDoctorId());
                    task.setDoctorName(appointment.getDoctorName());
                    task.setScheduledTime(appointmentDateTime);
                    task.setStatus(0); // 0表示待处理
                    task.setRetryCount(0);
                    task.setCreateBy(SecurityUtils.getUserId().toString());
                    task.setCreateTime(new Date());
                    task.setRemark("预约自动创建的定时任务");
                    
                    appointmentTaskService.insertAppointmentTask(task);
                    logger.info("为预约ID={}创建定时任务，计划执行时间: {}", 
                        appointment.getAppointmentId(), appointmentDateTime);
                } catch (Exception e) {
                    logger.error("创建预约任务失败: {}", e.getMessage(), e);
                    // 预约任务创建失败不影响预约本身，只记录日志
                }
            }
        } finally {
            lock.unlock(); // 释放锁
        }

        return R.ok("预约成功");
    }

    /**
     * 用户查询自己的预约记录
     * @param appointment
     * @return
     */
    @GetMapping("/getUserAppointment")
    public TableDataInfo getUserAppointment(Appointment appointment) {
        startPage();
        appointment.setPatientId(SecurityUtils.getUserId());
        List<Appointment> list = appointmentService.selectAppointmentList(appointment);
        return getDataTable(list);
    }

    /**
     * 用户查询自己的预约记录（可评论的）
     * @param appointment
     * @return
     */
    @GetMapping("/getUserCommentAppointment")
    public TableDataInfo getUserCommentAppointment(Appointment appointment) {
        startPage();
        appointment.setPatientId(SecurityUtils.getUserId());
        List<Appointment> list = appointmentService.getUserCommentAppointment(appointment);
        return getDataTable(list);
    }

    /**
     * 医生就诊处理
     * 就诊后同时删除医生与患者的好友关系
     */
    @Log(title = "医生就诊处理", businessType = BusinessType.UPDATE)
    @PutMapping("/outpatients")
    public R outpatients(@RequestBody Appointment appointment) {
        // 获取预约信息
        Appointment appointmentInfo = appointmentService.selectAppointmentByAppointmentId(appointment.getAppointmentId());
        
        // 验证就诊码
        if (!appointmentInfo.getCode().equals(appointment.getCode())) {
            return R.fail("就诊码错误!");
        }
        
        // 设置预约状态为已就诊(3)
        appointment.setAppointmentStatus(3);
        
        // 更新预约状态
        int result = appointmentService.updateAppointment(appointment);
        
        if (result > 0) {
            // 尝试删除好友关系
            try {
                boolean removed = friendRequestRemover.removeFriendRelation(
                    appointmentInfo.getPatientId(),
                    appointmentInfo.getDoctorId()
                );
                
                if (removed) {
                    logger.info("成功删除患者ID={}和医生ID={}的好友关系", 
                        appointmentInfo.getPatientId(), 
                        appointmentInfo.getDoctorId());
                } else {
                    logger.warn("删除患者ID={}和医生ID={}的好友关系失败", 
                        appointmentInfo.getPatientId(), 
                        appointmentInfo.getDoctorId());
                }
            } catch (Exception e) {
                logger.error("删除好友关系时发生异常: {}", e.getMessage(), e);
                // 删除好友关系失败不影响就诊处理，只记录日志
            }
            
            return R.ok("已完成就诊处理");
        } else {
            return R.fail("就诊处理失败，请重试");
        }
    }

    /**
     * 查询预约记录
     * @param appointmentId
     * @return
     */
    @GetMapping(value = "/getAppointment/{appointmentId}")
    public AjaxResult Appointment(@PathVariable("appointmentId") Long appointmentId) {
        return success(appointmentService.selectAppointmentByAppointmentId(appointmentId));
    }

    /**
     * 用户评论
     * @param appointment
     * @return
     */
    @PostMapping("/userComment")
    public R userComment(@RequestBody Appointment appointment) {
        appointment.setAppointmentStatus(5);
        appointment.setCreateBy(SecurityUtils.getUserId().toString());
        return R.ok(appointmentService.updateAppointment(appointment));
    }
    
    /**
     * 取消预约
     * @param appointmentId 预约ID
     * @return 处理结果
     */
    @Log(title = "取消预约", businessType = BusinessType.UPDATE)
    @PostMapping("/cancel/{appointmentId}")
    @Transactional(rollbackFor = Exception.class)
    public R cancelAppointment(@PathVariable("appointmentId") Long appointmentId) {
        lock.lock(); // 获取锁
        try {
            // 获取预约信息
            Appointment appointment = appointmentService.selectAppointmentByAppointmentId(appointmentId);
            if (appointment == null) {
                return R.fail("预约不存在");
            }
            
            // 验证是否是当前用户的预约
            Long currentUserId = SecurityUtils.getUserId();
            if (!appointment.getPatientId().equals(currentUserId)) {
                return R.fail("无权取消此预约");
            }
            
            // 验证预约状态是否允许取消
            if (appointment.getAppointmentStatus() != 1) {
                return R.fail("当前状态不允许取消预约");
            }
            
            // 更新预约状态为已取消(0)
            appointment.setAppointmentStatus(0);
            appointment.setUpdateBy(currentUserId.toString());
            appointment.setUpdateTime(new Date());
            appointment.setRemark("用户取消预约");
            
            int result = appointmentService.updateAppointment(appointment);
            
            if (result > 0) {
                // 恢复医生排班的余号数量
                DoctorScheduled doctorScheduled = doctorScheduledService.selectDoctorScheduledById(appointment.getDoctorScheduledId());
                if (doctorScheduled != null) {
                    doctorScheduled.setRemainder(doctorScheduled.getRemainder() + 1);
                    doctorScheduledService.updateDoctorScheduled(doctorScheduled);
                }
                
                // 删除相关的定时任务
                AppointmentTask query = new AppointmentTask();
                query.setAppointmentId(appointmentId);
                query.setStatus(0); // 只删除待处理的任务
                List<AppointmentTask> tasks = appointmentTaskService.selectAppointmentTaskList(query);
                
                for (AppointmentTask task : tasks) {
                    appointmentTaskService.deleteAppointmentTaskById(task.getTaskId());
                    logger.info("已删除预约ID={}的定时任务，任务ID={}", appointmentId, task.getTaskId());
                }
                
                return R.ok("预约已取消");
            } else {
                return R.fail("取消预约失败，请重试");
            }
        } catch (Exception e) {
            logger.error("取消预约时发生异常: {}", e.getMessage(), e);
            return R.fail("取消预约失败: " + e.getMessage());
        } finally {
            lock.unlock(); // 释放锁
        }
    }
}
