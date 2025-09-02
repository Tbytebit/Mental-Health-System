package com.doctor.survey.controller;

import java.util.*;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.doctor.common.annotation.Log;
import com.doctor.common.core.controller.BaseController;
import com.doctor.common.core.domain.AjaxResult;
import com.doctor.common.enums.BusinessType;
import com.doctor.survey.domain.SurveyQuestion;
import com.doctor.survey.service.ISurveyQuestionService;
import com.doctor.common.utils.poi.ExcelUtil;


/**
 * 调查问卷问题，用于存储问卷中的问题Controller
 *
 * @author doctor
 * @date 2024-01-21
 */
@RestController
@RequestMapping("/survey/question")
public class SurveyQuestionController extends BaseController
{
    @Autowired
    private ISurveyQuestionService surveyQuestionService;

    /**
     * 查询调查问卷问题，用于存储问卷中的问题列表
     */
    @GetMapping("/list/{questionnaireId}")
    public List<SurveyQuestion> list(@PathVariable String questionnaireId)
    {
        return surveyQuestionService.selectSurveyQuestionListByQuestionId(questionnaireId);
    }

    /**
     * 导出调查问卷问题，用于存储问卷中的问题列表
     */
    @Log(title = "调查问卷问题，用于存储问卷中的问题", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SurveyQuestion surveyQuestion)
    {
        List<SurveyQuestion> list = surveyQuestionService.selectSurveyQuestionList(surveyQuestion);
        ExcelUtil<SurveyQuestion> util = new ExcelUtil<SurveyQuestion>(SurveyQuestion.class);
        util.exportExcel(response, list, "调查问卷问题，用于存储问卷中的问题数据");
    }

    /**
     * 获取调查问卷问题，用于存储问卷中的问题详细信息
     */
    @GetMapping(value = "/{questionId}")
    public AjaxResult getInfo(@PathVariable("questionId") String questionId)
    {
        return success(surveyQuestionService.selectSurveyQuestionByQuestionId(questionId));
    }


    /**
     * 保存问题，用于存储问卷中的问题
     */

    @Log(title = "调查问卷问题，用于存储问卷中的问题", businessType = BusinessType.INSERT)
    @PostMapping("/save")
    public AjaxResult save(@RequestBody SurveyQuestion surveyQuestion)
    {
        if(Objects.equals(surveyQuestion.getQuestionId(), "")) {
            int tmp = surveyQuestionService.insertSurveyQuestion(surveyQuestion);
            if(tmp>0){
                return AjaxResult.success(tmp);
            }else {
                return AjaxResult.error();
            }
        } else {
            int tmp = surveyQuestionService.updateSurveyQuestion(surveyQuestion);
           return AjaxResult.success(tmp);
        }
    }

    /**
     * 删除调查问卷问题，用于存储问卷中的问题
     */

    @Log(title = "调查问卷问题，用于存储问卷中的问题", businessType = BusinessType.DELETE)
    @DeleteMapping("/del/{questionIds}")
    public AjaxResult remove(@PathVariable String[] questionIds)
    {
        return toAjax(surveyQuestionService.deleteSurveyQuestionByQuestionIds(questionIds));
    }
}
