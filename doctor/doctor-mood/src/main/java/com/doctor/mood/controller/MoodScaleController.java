package com.doctor.mood.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.doctor.mood.domain.entity.MoodScale;
import com.doctor.mood.service.IMoodScaleService;
import com.doctor.common.utils.poi.ExcelUtil;
import com.doctor.common.core.page.TableDataInfo;

/**
 * 心理情绪量表Controller
 */
@RestController
@RequestMapping("/mood/scale")
public class MoodScaleController extends BaseController
{
    @Autowired
    private IMoodScaleService moodScaleService;

    /**
     * 查询心理情绪量表列表
     */
    @PreAuthorize("@ss.hasPermi('mood:scale:list')")
    @GetMapping("/list")
    public TableDataInfo list(MoodScale moodScale)
    {
        startPage();
        List<MoodScale> list = moodScaleService.selectMoodScaleList(moodScale);
        return getDataTable(list);
    }
    
    /**
     * 查询所有有效的心理情绪量表列表（公共接口）
     */
    @GetMapping("/listAll")
    public AjaxResult listAll()
    {
        MoodScale moodScale = new MoodScale();
        moodScale.setStatus("1"); // 只查询状态正常的量表
        List<MoodScale> list = moodScaleService.selectMoodScaleList(moodScale);
        return success(list);
    }

    /**
     * 导出心理情绪量表列表
     */
    @PreAuthorize("@ss.hasPermi('mood:scale:export')")
    @Log(title = "心理情绪量表", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MoodScale moodScale)
    {
        List<MoodScale> list = moodScaleService.selectMoodScaleList(moodScale);
        ExcelUtil<MoodScale> util = new ExcelUtil<MoodScale>(MoodScale.class);
        util.exportExcel(response, list, "心理情绪量表数据");
    }

    /**
     * 获取心理情绪量表详细信息
     */
    @PreAuthorize("@ss.hasPermi('mood:scale:query')")
    @GetMapping(value = "/{scaleId}")
    public AjaxResult getInfo(@PathVariable("scaleId") Long scaleId)
    {
        return success(moodScaleService.selectMoodScaleByScaleId(scaleId));
    }
    
    /**
     * 获取心理情绪量表详细信息（公共接口）
     */
    @GetMapping(value = "/info/{scaleId}")
    public AjaxResult getPublicInfo(@PathVariable("scaleId") Long scaleId)
    {
        return success(moodScaleService.selectMoodScaleByScaleId(scaleId));
    }

    /**
     * 新增心理情绪量表
     */
    @PreAuthorize("@ss.hasPermi('mood:scale:add')")
    @Log(title = "心理情绪量表", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MoodScale moodScale)
    {
        return toAjax(moodScaleService.insertMoodScale(moodScale));
    }

    /**
     * 修改心理情绪量表
     */
    @PreAuthorize("@ss.hasPermi('mood:scale:edit')")
    @Log(title = "心理情绪量表", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MoodScale moodScale)
    {
        return toAjax(moodScaleService.updateMoodScale(moodScale));
    }

    /**
     * 删除心理情绪量表
     */
    @PreAuthorize("@ss.hasPermi('mood:scale:remove')")
    @Log(title = "心理情绪量表", businessType = BusinessType.DELETE)
    @DeleteMapping("/{scaleIds}")
    public AjaxResult remove(@PathVariable Long[] scaleIds)
    {
        return toAjax(moodScaleService.deleteMoodScaleByScaleIds(scaleIds));
    }
}