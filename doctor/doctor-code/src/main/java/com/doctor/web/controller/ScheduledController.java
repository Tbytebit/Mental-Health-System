package com.doctor.web.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.doctor.common.utils.SecurityUtils;
import com.doctor.web.domain.vo.ScheduledVo;
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
import com.doctor.web.domain.Scheduled;
import com.doctor.web.service.IScheduledService;
import com.doctor.common.utils.poi.ExcelUtil;
import com.doctor.common.core.page.TableDataInfo;

/**
 * 预约排版Controller
 * 
 * @author Li
 * @date 2024-05-13
 */
@RestController
@RequestMapping("/maincode/scheduled")
public class ScheduledController extends BaseController
{
    @Autowired
    private IScheduledService scheduledService;

    /**
     * 查询预约排版列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:scheduled:list')")
    @GetMapping("/list")
    public TableDataInfo list(ScheduledVo scheduled)
    {
        if (SecurityUtils.getUserId() != 1L){
            scheduled.setDoctorId(SecurityUtils.getUserId());
        }
        startPage();
        List<ScheduledVo> list = scheduledService.selectScheduledList(scheduled);
        return getDataTable(list);
    }

    /**
     * 导出预约排版列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:scheduled:export')")
    @Log(title = "预约排版", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, ScheduledVo scheduled)
    {
        List<ScheduledVo> list = scheduledService.selectScheduledList(scheduled);
        ExcelUtil<ScheduledVo> util = new ExcelUtil<ScheduledVo>(ScheduledVo.class);
        util.exportExcel(response, list, "预约排版数据");
    }

    /**
     * 获取预约排版详细信息
     */
    @PreAuthorize("@ss.hasPermi('maincode:scheduled:query')")
    @GetMapping(value = "/{scheduledId}")
    public AjaxResult getInfo(@PathVariable("scheduledId") Long scheduledId)
    {
        return success(scheduledService.selectScheduledByScheduledId(scheduledId));
    }

    /**
     * 新增预约排版
     */
    @PreAuthorize("@ss.hasPermi('maincode:scheduled:add')")
    @Log(title = "预约排版", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Scheduled scheduled)
    {
        return toAjax(scheduledService.insertScheduled(scheduled));
    }

    /**
     * 修改预约排版
     */
    @PreAuthorize("@ss.hasPermi('maincode:scheduled:edit')")
    @Log(title = "预约排版", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Scheduled scheduled)
    {
        return toAjax(scheduledService.updateScheduled(scheduled));
    }

    /**
     * 删除预约排版
     */
    @PreAuthorize("@ss.hasPermi('maincode:scheduled:remove')")
    @Log(title = "预约排版", businessType = BusinessType.DELETE)
	@DeleteMapping("/{scheduledIds}")
    public AjaxResult remove(@PathVariable Long[] scheduledIds)
    {
        return toAjax(scheduledService.deleteScheduledByScheduledIds(scheduledIds));
    }

    /**
     * 查询医生预约列表
     * @param scheduled
     * @return
     */
    @GetMapping("/getScheduledList")
    public TableDataInfo getScheduledList(ScheduledVo scheduled)
    {
        startPage();
        scheduled.setStatus(1L);
        scheduled.setDeptId(105L);
        List<ScheduledVo> list = scheduledService.getCheduledList(scheduled);
        return getDataTable(list);
    }

    /**
     * 查询治疗屋预约列表
     * @param scheduled
     * @return
     */
    @GetMapping("/getRoomScheduledList")
    public TableDataInfo getRoomScheduledList(ScheduledVo scheduled)
    {
        startPage();
        scheduled.setStatus(1L);
        scheduled.setDeptId(107L);
        List<ScheduledVo> list = scheduledService.getCheduledList(scheduled);
        return getDataTable(list);
    }


}
