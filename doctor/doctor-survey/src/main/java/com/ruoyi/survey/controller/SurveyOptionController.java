package com.doctor.survey.controller;

import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletResponse;
import javax.swing.text.html.Option;

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
import com.doctor.survey.domain.SurveyOption;
import com.doctor.survey.service.ISurveyOptionService;
import com.doctor.common.utils.poi.ExcelUtil;
import com.doctor.common.core.page.TableDataInfo;

/**
 * 调查问卷问题选项存储Controller
 *
 * @author doctor
 * @date 2024-01-21
 */
@RestController
@RequestMapping("/survey/option")
public class SurveyOptionController extends BaseController
{
    @Autowired
    private ISurveyOptionService surveyOptionService;

    /**
     * 查询调查问卷问题选项存储列表
     */

    @GetMapping("/list/{questionId}")
    public List<SurveyOption> list(@PathVariable String questionId)
    {
        return surveyOptionService.selectSurveyOptionListByQuestionId(questionId);
    }

    /**
     * 导出调查问卷问题选项存储列表
     */
    @Log(title = "调查问卷问题选项存储", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SurveyOption surveyOption)
    {
        List<SurveyOption> list = surveyOptionService.selectSurveyOptionList(surveyOption);
        ExcelUtil<SurveyOption> util = new ExcelUtil<SurveyOption>(SurveyOption.class);
        util.exportExcel(response, list, "调查问卷问题选项存储数据");
    }

    /**
     * 获取调查问卷问题选项存储详细信息
     */

    @GetMapping(value = "/{optionId}")
    public AjaxResult getInfo(@PathVariable("optionId") String optionId)
    {
        return success(surveyOptionService.selectSurveyOptionByOptionId(optionId));
    }

    /**
     * 保存调查问卷问题选项存储
     */

    @Log(title = "调查问卷问题选项存储", businessType = BusinessType.INSERT)
    @PostMapping("/save")
    public AjaxResult save(@RequestBody SurveyOption surveyOption)
    {
        if(Objects.equals(surveyOption.getOptionId(), ""))
            return toAjax(surveyOptionService.insertSurveyOption(surveyOption));
        else
            return toAjax(surveyOptionService.updateSurveyOption(surveyOption));
    }



    /**
     * 删除调查问卷问题选项存储
     */

    @Log(title = "调查问卷问题选项存储", businessType = BusinessType.DELETE)
    @DeleteMapping("del/{optionIds}")
    public AjaxResult remove(@PathVariable String[] optionIds)
    {
        return toAjax(surveyOptionService.deleteSurveyOptionByOptionIds(optionIds));
    }
}
