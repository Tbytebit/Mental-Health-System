package com.doctor.web.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.doctor.common.core.domain.R;
import com.doctor.common.utils.SecurityUtils;
import com.doctor.web.domain.vo.DoctorScheduledVo;
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
import com.doctor.web.domain.DoctorScheduled;
import com.doctor.web.service.IDoctorScheduledService;
import com.doctor.common.utils.poi.ExcelUtil;
import com.doctor.common.core.page.TableDataInfo;

/**
 * 医生预约排版Controller
 * 
 * @author Li
 * @date 2024-05-13
 */
@RestController
@RequestMapping("/maincode/doctorScheduled")
public class DoctorScheduledController extends BaseController
{
    @Autowired
    private IDoctorScheduledService doctorScheduledService;

    /**
     * 查询医生预约排版列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:doctorScheduled:list')")
    @GetMapping("/list")
    public TableDataInfo list(DoctorScheduledVo doctorScheduled)
    {
        if (SecurityUtils.getUserId() != 1L){
            doctorScheduled.setDoctorId(SecurityUtils.getUserId());
        }
        startPage();
        List<DoctorScheduledVo> list = doctorScheduledService.selectDoctorScheduledList(doctorScheduled);
        return getDataTable(list);
    }

    /**
     * 导出医生预约排版列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:doctorScheduled:export')")
    @Log(title = "医生预约排版", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DoctorScheduledVo doctorScheduled)
    {
        List<DoctorScheduledVo> list = doctorScheduledService.selectDoctorScheduledList(doctorScheduled);
        ExcelUtil<DoctorScheduledVo> util = new ExcelUtil<DoctorScheduledVo>(DoctorScheduledVo.class);
        util.exportExcel(response, list, "医生预约排版数据");
    }

    /**
     * 获取医生预约排版详细信息
     */
    @PreAuthorize("@ss.hasPermi('maincode:doctorScheduled:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(doctorScheduledService.selectDoctorScheduledById(id));
    }

    /**
     * 新增医生预约排版
     */
    @PreAuthorize("@ss.hasPermi('maincode:doctorScheduled:add')")
    @Log(title = "医生预约排版", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DoctorScheduled doctorScheduled)
    {
        return toAjax(doctorScheduledService.insertDoctorScheduled(doctorScheduled));
    }

    /**
     * 修改医生预约排版
     */
    @PreAuthorize("@ss.hasPermi('maincode:doctorScheduled:edit')")
    @Log(title = "医生预约排版", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DoctorScheduled doctorScheduled)
    {
        return toAjax(doctorScheduledService.updateDoctorScheduled(doctorScheduled));
    }

    /**
     * 删除医生预约排版
     */
    @PreAuthorize("@ss.hasPermi('maincode:doctorScheduled:remove')")
    @Log(title = "医生预约排版", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids)
    {
        return toAjax(doctorScheduledService.deleteDoctorScheduledByIds(ids));
    }

    /**
     * 查询详情列表
     * @param doctorScheduled
     * @return
     */
    @GetMapping("/getDoctorScheduledList")
    public R getDoctorScheduledList(DoctorScheduledVo doctorScheduled)  {
        doctorScheduled.setStatus(1L);
        List<DoctorScheduledVo> list = doctorScheduledService.getDoctorScheduledList(doctorScheduled);
        return R.ok(list);
    }

    /**
     * 查询详情
     * @param id
     * @return
     */
    @GetMapping(value = "/getDoctorScheduled/{id}")
    public AjaxResult getDoctorScheduled(@PathVariable("id") Long id)
    {
        return success(doctorScheduledService.selectDoctorScheduledById(id));
    }


}
