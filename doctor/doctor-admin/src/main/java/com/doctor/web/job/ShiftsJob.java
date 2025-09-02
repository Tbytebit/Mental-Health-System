package com.doctor.web.job;

import com.doctor.common.core.domain.entity.SysUser;
import com.doctor.system.service.ISysUserService;
import com.doctor.web.domain.DoctorScheduled;
import com.doctor.web.domain.Scheduled;
import com.doctor.web.service.IDoctorScheduledService;
import com.doctor.web.service.IScheduledService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ShiftsJob {
    @Autowired
    private ISysUserService userService;

    @Autowired
    private IScheduledService scheduledService;
    @Autowired
    private IDoctorScheduledService doctorScheduledService;

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 6 * * ?")
//    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000) // 60000毫秒 = 1分钟
    @Transactional(rollbackFor = Exception.class)
    public void job() throws Exception {
        System.out.println("任务" + new Date());
        SysUser user = new SysUser();
        user.setDeptId(105L); // 部门
        List<SysUser> list = userService.selectDoctorList(user);
        for (SysUser sysUser:  list ) {
            // 获取明天日期
            // 获取明天日期的开始时间
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.setTime(new Date());
            tomorrow.add(Calendar.DATE, 1);
            tomorrow.set(Calendar.HOUR_OF_DAY, 0);
            tomorrow.set(Calendar.MINUTE, 0);
            tomorrow.set(Calendar.SECOND, 0);
            tomorrow.set(Calendar.MILLISECOND, 0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // 插入排班
            Scheduled scheduled = new Scheduled();
            scheduled.setDoctorId(sysUser.getUserId());
            scheduled.setTime(tomorrow.getTime());
            scheduled.setStatus(1L);
            scheduled.setScheduledId(System.currentTimeMillis());
            scheduledService.insertScheduled(scheduled);
            // 插入详情1
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            DoctorScheduled doctorScheduled = new DoctorScheduled();
            Date tomorrowDate = tomorrow.getTime();
            String startTime = dateFormat.format(tomorrowDate) + " 08:00:00";
            String endTime = dateFormat.format(tomorrowDate) + " 12:00:00";
            doctorScheduled.setStartTime(simpleDateFormat.parse(startTime));
            doctorScheduled.setEndTime(simpleDateFormat.parse(endTime));
            doctorScheduled.setDoctorId(sysUser.getUserId());
            doctorScheduled.setTime(tomorrowDate);
            doctorScheduled.setNumber(1);
            doctorScheduled.setRemainder(1);
            doctorScheduled.setStatus(1L);
            doctorScheduled.setScheduledId(scheduled.getScheduledId());
            doctorScheduledService.insertDoctorScheduled(doctorScheduled);

            DoctorScheduled doctorScheduled1 = new DoctorScheduled();
            String startTime1 = dateFormat.format(tomorrowDate) + " 14:00:00";
            String endTime2 = dateFormat.format(tomorrowDate) + " 17:00:00";
            doctorScheduled1.setStartTime(simpleDateFormat.parse(startTime1));
            doctorScheduled1.setEndTime(simpleDateFormat.parse(endTime2));
            doctorScheduled1.setDoctorId(sysUser.getUserId());
            doctorScheduled1.setTime(tomorrowDate);
            doctorScheduled1.setNumber(1);
            doctorScheduled1.setRemainder(1);
            doctorScheduled1.setStatus(1L);
            doctorScheduled1.setScheduledId(scheduled.getScheduledId());
            doctorScheduledService.insertDoctorScheduled(doctorScheduled1);
        }
    }

    @org.springframework.scheduling.annotation.Scheduled(cron = "0 0 5 * * ?")
//    @org.springframework.scheduling.annotation.Scheduled(fixedRate = 60000) // 60000毫秒 = 1分钟
    @Transactional(rollbackFor = Exception.class)
    public void job1() throws Exception {
        System.out.println("任务" + new Date());
        SysUser user = new SysUser();
        user.setDeptId(107L); // 部门
        List<SysUser> list = userService.selectDoctorList(user);
        for (SysUser sysUser:  list ) {
            // 获取明天日期
            // 获取明天日期的开始时间
            Calendar tomorrow = Calendar.getInstance();
            tomorrow.setTime(new Date());
            tomorrow.add(Calendar.DATE, 1);
            tomorrow.set(Calendar.HOUR_OF_DAY, 0);
            tomorrow.set(Calendar.MINUTE, 0);
            tomorrow.set(Calendar.SECOND, 0);
            tomorrow.set(Calendar.MILLISECOND, 0);
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");

            // 插入排班
            Scheduled scheduled = new Scheduled();
            scheduled.setDoctorId(sysUser.getUserId());
            scheduled.setTime(tomorrow.getTime());
            scheduled.setStatus(1L);
            scheduled.setScheduledId(System.currentTimeMillis());
            scheduledService.insertScheduled(scheduled);
            // 插入详情1
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            DoctorScheduled doctorScheduled = new DoctorScheduled();
            Date tomorrowDate = tomorrow.getTime();
            String startTime = dateFormat.format(tomorrowDate) + " 08:00:00";
            String endTime = dateFormat.format(tomorrowDate) + " 10:00:00";
            doctorScheduled.setStartTime(simpleDateFormat.parse(startTime));
            doctorScheduled.setEndTime(simpleDateFormat.parse(endTime));
            doctorScheduled.setDoctorId(sysUser.getUserId());
            doctorScheduled.setTime(tomorrowDate);
            doctorScheduled.setNumber(1);
            doctorScheduled.setRemainder(1);
            doctorScheduled.setStatus(1L);
            doctorScheduled.setScheduledId(scheduled.getScheduledId());
            doctorScheduledService.insertDoctorScheduled(doctorScheduled);

            DoctorScheduled doctorScheduled1 = new DoctorScheduled();
            String startTime1 = dateFormat.format(tomorrowDate) + " 10:00:00";
            String endTime1 = dateFormat.format(tomorrowDate) + " 12:00:00";
            doctorScheduled1.setStartTime(simpleDateFormat.parse(startTime1));
            doctorScheduled1.setEndTime(simpleDateFormat.parse(endTime1));
            doctorScheduled1.setDoctorId(sysUser.getUserId());
            doctorScheduled1.setTime(tomorrowDate);
            doctorScheduled1.setNumber(1);
            doctorScheduled1.setRemainder(1);
            doctorScheduled1.setStatus(1L);
            doctorScheduled1.setScheduledId(scheduled.getScheduledId());
            doctorScheduledService.insertDoctorScheduled(doctorScheduled1);


            DoctorScheduled doctorScheduled2 = new DoctorScheduled();
            String startTime2 = dateFormat.format(tomorrowDate) + " 14:00:00";
            String endTime2 = dateFormat.format(tomorrowDate) + " 16:00:00";
            doctorScheduled2.setStartTime(simpleDateFormat.parse(startTime2));
            doctorScheduled2.setEndTime(simpleDateFormat.parse(endTime2));
            doctorScheduled2.setDoctorId(sysUser.getUserId());
            doctorScheduled2.setTime(tomorrowDate);
            doctorScheduled2.setNumber(1);
            doctorScheduled2.setRemainder(1);
            doctorScheduled2.setStatus(1L);
            doctorScheduled2.setScheduledId(scheduled.getScheduledId());
            doctorScheduledService.insertDoctorScheduled(doctorScheduled2);


            DoctorScheduled doctorScheduled3 = new DoctorScheduled();
            String startTime3 = dateFormat.format(tomorrowDate) + " 16:00:00";
            String endTime3 = dateFormat.format(tomorrowDate) + " 18:00:00";
            doctorScheduled3.setStartTime(simpleDateFormat.parse(startTime3));
            doctorScheduled3.setEndTime(simpleDateFormat.parse(endTime3));
            doctorScheduled3.setDoctorId(sysUser.getUserId());
            doctorScheduled3.setTime(tomorrowDate);
            doctorScheduled3.setNumber(1);
            doctorScheduled3.setRemainder(1);
            doctorScheduled3.setStatus(1L);
            doctorScheduled3.setScheduledId(scheduled.getScheduledId());
            doctorScheduledService.insertDoctorScheduled(doctorScheduled3);
        }
    }





}
