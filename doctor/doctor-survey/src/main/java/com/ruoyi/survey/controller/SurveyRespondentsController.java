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
import com.doctor.survey.domain.SurveyRespondents;
import com.doctor.survey.service.ISurveyRespondentsService;
import com.doctor.common.utils.poi.ExcelUtil;
import com.doctor.common.core.page.TableDataInfo;

/**
 * 问卷答卷存储Controller
 *
 * @author doctor
 * @date 2024-01-22
 */
@RestController
@RequestMapping("/survey/respondents")
public class SurveyRespondentsController extends BaseController
{
    @Autowired
    private ISurveyRespondentsService surveyRespondentsService;

    /**
     * 查询问卷答卷存储列表
     */

    @GetMapping("/list")
    public TableDataInfo list(SurveyRespondents surveyRespondents)
    {
        startPage();
        List<SurveyRespondents> list = surveyRespondentsService.selectSurveyRespondentsListByQuestionnaireId(surveyRespondents.getQuestionnaireId());
        return getDataTable(list);
    }

    /**
     * 导出问卷答卷存储列表
     */

    @Log(title = "问卷答卷存储", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SurveyRespondents surveyRespondents)
    {
        List<SurveyRespondents> list = surveyRespondentsService.selectSurveyRespondentsList(surveyRespondents);
        ExcelUtil<SurveyRespondents> util = new ExcelUtil<SurveyRespondents>(SurveyRespondents.class);
        util.exportExcel(response, list, "问卷答卷存储数据");
    }

    /**
     * 获取问卷答卷存储详细信息
     */

    @GetMapping(value = "/{respondentsId}")
    public AjaxResult getInfo(@PathVariable("respondentsId") String respondentsId)
    {
        return success(surveyRespondentsService.selectSurveyRespondentsByRespondentsId(respondentsId));
    }

    /**
     * 新增问卷答卷存储
     */

    @Log(title = "问卷答卷存储", businessType = BusinessType.INSERT)
    @PostMapping("/add")
    public AjaxResult add(@RequestBody SurveyRespondents surveyRespondents)
    {
        surveyRespondentsService.insertSurveyRespondents(surveyRespondents);
        return AjaxResult.success("操作成功",surveyRespondents.getRespondentsId());
    }

    /**
     * 修改问卷答卷存储
     */

    @Log(title = "问卷答卷存储", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SurveyRespondents surveyRespondents)
    {
        return toAjax(surveyRespondentsService.updateSurveyRespondents(surveyRespondents));
    }

    /**
     * 删除问卷答卷存储
     */

    @Log(title = "问卷答卷存储", businessType = BusinessType.DELETE)
    @DeleteMapping("/del/{respondentsId}")
    public AjaxResult remove(@PathVariable String respondentsId)
    {
        return toAjax(surveyRespondentsService.deleteSurveyRespondentsByRespondentsId(respondentsId));
    }


    @GetMapping(value = "/survey/{respondentsId}")
    public AjaxResult getSurvey(@PathVariable("respondentsId") String respondentsId)
    {
        return success(surveyRespondentsService.selectSurveyInfoByRespondentsId(respondentsId));
    }
}
