package com.doctor.survey.controller;

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
import com.doctor.survey.domain.SurveyAnswer;
import com.doctor.survey.service.ISurveyAnswerService;
import com.doctor.common.utils.poi.ExcelUtil;
import com.doctor.common.core.page.TableDataInfo;

/**
 * 答卷的问题答案存储Controller
 *
 * @author doctor
 * @date 2024-01-22
 */
@RestController
@RequestMapping("/survey/answer")
public class SurveyAnswerController extends BaseController
{
    @Autowired
    private ISurveyAnswerService surveyAnswerService;

    /**
     * 查询答卷的问题答案存储列表
     */

    @GetMapping("/list")
    public TableDataInfo list(SurveyAnswer surveyAnswer)
    {
        startPage();
        List<SurveyAnswer> list = surveyAnswerService.selectSurveyAnswerList(surveyAnswer);
        return getDataTable(list);
    }

    /**
     * 导出答卷的问题答案存储列表
     */

    @Log(title = "答卷的问题答案存储", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SurveyAnswer surveyAnswer)
    {
        List<SurveyAnswer> list = surveyAnswerService.selectSurveyAnswerList(surveyAnswer);
        ExcelUtil<SurveyAnswer> util = new ExcelUtil<SurveyAnswer>(SurveyAnswer.class);
        util.exportExcel(response, list, "答卷的问题答案存储数据");
    }

    /**
     * 获取答卷的问题答案存储详细信息
     */

    @GetMapping(value = "/{answerId}")
    public AjaxResult getInfo(@PathVariable("answerId") String answerId)
    {
        return success(surveyAnswerService.selectSurveyAnswerByAnswerId(answerId));
    }

    /**
     * 新增答卷的问题答案存储
     */

    @Log(title = "答卷的问题答案存储", businessType = BusinessType.INSERT)
    @PostMapping("add")
    public AjaxResult add(@RequestBody SurveyAnswer surveyAnswer)
    {
        return toAjax(surveyAnswerService.insertSurveyAnswer(surveyAnswer));
    }

    /**
     * 修改答卷的问题答案存储
     */
    @PreAuthorize("@ss.hasPermi('survey:answer:edit')")
    @Log(title = "答卷的问题答案存储", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SurveyAnswer surveyAnswer)
    {
        return toAjax(surveyAnswerService.updateSurveyAnswer(surveyAnswer));
    }

    /**
     * 删除答卷的问题答案存储
     */
    @PreAuthorize("@ss.hasPermi('survey:answer:remove')")
    @Log(title = "答卷的问题答案存储", businessType = BusinessType.DELETE)
    @DeleteMapping("/{answerIds}")
    public AjaxResult remove(@PathVariable String[] answerIds)
    {
        return toAjax(surveyAnswerService.deleteSurveyAnswerByAnswerIds(answerIds));
    }
}
