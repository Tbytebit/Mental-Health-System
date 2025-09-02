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
import com.doctor.mood.domain.entity.MoodQuestion;
import com.doctor.mood.service.IMoodQuestionService;
import com.doctor.common.utils.poi.ExcelUtil;
import com.doctor.common.core.page.TableDataInfo;

/**
 * 心理量表问题Controller
 */
@RestController
@RequestMapping("/mood/question")
public class MoodQuestionController extends BaseController
{
    @Autowired
    private IMoodQuestionService moodQuestionService;

    /**
     * 查询心理量表问题列表
     */
    @PreAuthorize("@ss.hasPermi('mood:question:list')")
    @GetMapping("/list")
    public TableDataInfo list(MoodQuestion moodQuestion)
    {
        startPage();
        List<MoodQuestion> list = moodQuestionService.selectMoodQuestionList(moodQuestion);
        return getDataTable(list);
    }
    
    /**
     * 查询指定量表的问题列表（公共接口）
     */
    @GetMapping("/listByScale/{scaleId}")
    public AjaxResult listByScale(@PathVariable("scaleId") Long scaleId)
    {
        List<MoodQuestion> list = moodQuestionService.selectMoodQuestionByScaleId(scaleId);
        return success(list);
    }

    /**
     * 导出心理量表问题列表
     */
    @PreAuthorize("@ss.hasPermi('mood:question:export')")
    @Log(title = "心理量表问题", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MoodQuestion moodQuestion)
    {
        List<MoodQuestion> list = moodQuestionService.selectMoodQuestionList(moodQuestion);
        ExcelUtil<MoodQuestion> util = new ExcelUtil<MoodQuestion>(MoodQuestion.class);
        util.exportExcel(response, list, "心理量表问题数据");
    }

    /**
     * 获取心理量表问题详细信息
     */
    @PreAuthorize("@ss.hasPermi('mood:question:query')")
    @GetMapping(value = "/{questionId}")
    public AjaxResult getInfo(@PathVariable("questionId") Long questionId)
    {
        return success(moodQuestionService.selectMoodQuestionByQuestionId(questionId));
    }

    /**
     * 新增心理量表问题
     */
    @PreAuthorize("@ss.hasPermi('mood:question:add')")
    @Log(title = "心理量表问题", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MoodQuestion moodQuestion)
    {
        return toAjax(moodQuestionService.insertMoodQuestion(moodQuestion));
    }

    /**
     * 修改心理量表问题
     */
    @PreAuthorize("@ss.hasPermi('mood:question:edit')")
    @Log(title = "心理量表问题", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MoodQuestion moodQuestion)
    {
        return toAjax(moodQuestionService.updateMoodQuestion(moodQuestion));
    }

    /**
     * 删除心理量表问题
     */
    @PreAuthorize("@ss.hasPermi('mood:question:remove')")
    @Log(title = "心理量表问题", businessType = BusinessType.DELETE)
    @DeleteMapping("/{questionIds}")
    public AjaxResult remove(@PathVariable Long[] questionIds)
    {
        return toAjax(moodQuestionService.deleteMoodQuestionByQuestionIds(questionIds));
    }
}