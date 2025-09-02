package com.doctor.web.controller;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.doctor.common.core.domain.R;
import com.doctor.common.utils.SecurityUtils;
import com.doctor.web.domain.Activity;
import com.doctor.web.service.IActivityService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
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
import com.doctor.web.domain.ActivityReservation;
import com.doctor.web.service.IActivityReservationService;
import com.doctor.common.utils.poi.ExcelUtil;
import com.doctor.common.core.page.TableDataInfo;

/**
 * 活动预约Controller
 * 
 * @author ruoyi
 * @date 2024-05-24
 */
@RestController
@RequestMapping("/maincode/reservation")
public class ActivityReservationController extends BaseController
{
    @Autowired
    private IActivityReservationService activityReservationService;

    @Autowired
    private IActivityService activityService;

    /**
     * 查询活动预约列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:reservation:list')")
    @GetMapping("/list")
    public TableDataInfo list(ActivityReservation activityReservation)
    {
        startPage();
        List<ActivityReservation> list = activityReservationService.selectActivityReservationList(activityReservation);
        return getDataTable(list);
    }

    /**
     * 导出活动预约列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:reservation:export')")
    @Log(title = "活动预约", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ActivityReservation activityReservation)
    {
        List<ActivityReservation> list = activityReservationService.selectActivityReservationList(activityReservation);
        ExcelUtil<ActivityReservation> util = new ExcelUtil<ActivityReservation>(ActivityReservation.class);
        util.exportExcel(response, list, "活动预约数据");
    }

    /**
     * 获取活动预约详细信息
     */
    @PreAuthorize("@ss.hasPermi('maincode:reservation:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(activityReservationService.selectActivityReservationById(id));
    }

    /**
     * 新增活动预约
     */
    @PreAuthorize("@ss.hasPermi('maincode:reservation:add')")
    @Log(title = "活动预约", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody ActivityReservation activityReservation)
    {
        activityReservation.setCreateTime(new Date());
        activityReservation.setCreateBy(SecurityUtils.getUserId().toString());
        return toAjax(activityReservationService.insertActivityReservation(activityReservation));
    }

    /**
     * 修改活动预约
     */
    @PreAuthorize("@ss.hasPermi('maincode:reservation:edit')")
    @Log(title = "活动预约", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody ActivityReservation activityReservation)
    {
        return toAjax(activityReservationService.updateActivityReservation(activityReservation));
    }

    /**
     * 删除活动预约
     */
    @PreAuthorize("@ss.hasPermi('maincode:reservation:remove')")
    @Log(title = "活动预约", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(activityReservationService.deleteActivityReservationByIds(ids));
    }

    /**
     * 预约活动
     * @param activityReservation
     * @return
     */
    @Log(title = "用户预约活动", businessType = BusinessType.INSERT)
    @PostMapping("/addActivityReservation")
    public R addActivityReservation(@RequestBody ActivityReservation activityReservation) {
        activityReservation.setUserId(SecurityUtils.getUserId());
        List<ActivityReservation> list = activityReservationService.selectActivityReservationList(activityReservation);
        if (list.size() >= 1) {
           return R.fail("活动已经存在预约了！");
        }
        Activity activity = activityService.selectActivityById(activityReservation.getActivityId());
        if (activity==null) {
            return R.fail("活动无效！");
        }
        activityReservation.setCreateTime(new Date());
        activityReservation.setCreateBy(SecurityUtils.getUserId().toString());
        activityReservation.setName(activity.getName());
        activityReservation.setDetails(activity.getDetails());
        activityReservation.setStartTime(activity.getStartTime());
        activityReservation.setEndTime(activity.getEndTime());
        activityReservation.setLocation(activity.getLocation());
        activityReservation.setCover(activity.getCover());
        return R.ok(activityReservationService.insertActivityReservation(activityReservation));
    }

    /**
     * 查询预约列表
     * @param activityReservation
     * @return
     */
    @GetMapping("/getReservationList")
    public TableDataInfo getReservationList(ActivityReservation activityReservation)
    {
        activityReservation.setUserId(SecurityUtils.getUserId());
        startPage();
        List<ActivityReservation> list = activityReservationService.selectActivityReservationList(activityReservation);
        return getDataTable(list);
    }


    @Log(title = "活动预约", businessType = BusinessType.DELETE)
    @GetMapping("/deleteActivityReservation/{id}")
    public AjaxResult deleteActivityReservation(@PathVariable("id") Long id)
    {
        return toAjax(activityReservationService.deleteActivityReservationById(id));
    }


}
