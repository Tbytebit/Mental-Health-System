package com.doctor.survey.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.doctor.common.core.domain.entity.SysUser;
import com.doctor.survey.domain.SurveyUserQuestionnaire;
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
import com.doctor.common.core.domain.entity.SysUser;
import com.doctor.common.core.controller.BaseController;
import com.doctor.common.core.domain.AjaxResult;
import com.doctor.common.enums.BusinessType;
import com.doctor.survey.domain.SurveyQuestionnaire;
import com.doctor.survey.service.ISurveyQuestionnaireService;
import com.doctor.common.utils.poi.ExcelUtil;
import com.doctor.common.core.page.TableDataInfo;

/**
 * 问卷中心Controller
 * 
 * @author guo
 * @date 2024-01-19
 */
@RestController
@RequestMapping("/survey/questionnaire")
public class SurveyQuestionnaireController extends BaseController
{
    @Autowired
    private ISurveyQuestionnaireService surveyQuestionnaireService;

    /**
     * 查询问卷中心列表
     */

    @GetMapping("/list")
    public TableDataInfo list(SurveyQuestionnaire surveyQuestionnaire)
    {
        startPage();
        List<SurveyQuestionnaire> list = surveyQuestionnaireService.selectSurveyQuestionnaireList(surveyQuestionnaire);
        return getDataTable(list);
    }

    /**
     * 导出问卷中心列表
     */
    @Log(title = "问卷中心", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, SurveyQuestionnaire surveyQuestionnaire)
    {
        List<SurveyQuestionnaire> list = surveyQuestionnaireService.selectSurveyQuestionnaireList(surveyQuestionnaire);
        ExcelUtil<SurveyQuestionnaire> util = new ExcelUtil<SurveyQuestionnaire>(SurveyQuestionnaire.class);
        util.exportExcel(response, list, "问卷中心数据");
    }

    /**
     * 获取问卷中心详细信息
     */

    @GetMapping(value = "/{questionnaireId}")
    public AjaxResult getInfo(@PathVariable("questionnaireId") String questionnaireId)
    {
        return success(surveyQuestionnaireService.selectSurveyQuestionnaireByQuestionnaireId(questionnaireId));
    }

    /**
     * 新增问卷中心
     */

    @Log(title = "问卷中心", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SurveyQuestionnaire surveyQuestionnaire)
    {
        return toAjax(surveyQuestionnaireService.insertSurveyQuestionnaire(surveyQuestionnaire));
    }

    /**
     * 修改问卷中心
     */
    @Log(title = "问卷中心", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SurveyQuestionnaire surveyQuestionnaire)
    {
        return toAjax(surveyQuestionnaireService.updateSurveyQuestionnaire(surveyQuestionnaire));
    }

    /**
     * 删除问卷中心
     */

    @Log(title = "问卷中心", businessType = BusinessType.DELETE)
	@DeleteMapping("/{questionnaireIds}")
    public AjaxResult remove(@PathVariable String[] questionnaireIds)
    {
        return toAjax(surveyQuestionnaireService.deleteSurveyQuestionnaireByQuestionnaireIds(questionnaireIds));
    }

    @Log(title = "问卷中心", businessType = BusinessType.UPDATE)
    @PutMapping("/publish/{questionnaireId}")
    public AjaxResult publish(@PathVariable String questionnaireId)
    {
        return toAjax(surveyQuestionnaireService.publishSurveyQuestionnaire(questionnaireId));
    }

    @GetMapping(value = "/stats/{questionnaireId}")
    public AjaxResult getRespondentsStats(@PathVariable("questionnaireId") String questionnaireId){
        return success(surveyQuestionnaireService.getDataStats(questionnaireId));
    }

    /**
     * 获取问卷中心详细信息
     */
    @PutMapping(value = "/status")
    public AjaxResult updateStatus(String questionnaireId,String status)
    {
        return success(surveyQuestionnaireService.updateSurveyQuestionnaireStatus(questionnaireId,status));
    }

    @GetMapping("/asUser/asList/{questionnaireId}")
    public TableDataInfo assignList(SysUser user,@PathVariable("questionnaireId") String questionnaireId)
    {
        startPage();
        List<SysUser> list = surveyQuestionnaireService.selectAssignedList(user,questionnaireId);
        return getDataTable(list);
    }

    @GetMapping("/asUser/unasList/{questionnaireId}")
    public TableDataInfo unAssignList(SysUser user,@PathVariable("questionnaireId") String questionnaireId)
    {
        startPage();
        List<SysUser> list = surveyQuestionnaireService.selectUnAssignedList(user,questionnaireId);
        return getDataTable(list);
    }

    @PutMapping("/asUser/selectAll")
    public AjaxResult selectAuthUserAll(String questionnaireId, String[] userIds)
    {
        return toAjax(surveyQuestionnaireService.insertAsUsers(questionnaireId, userIds));
    }

    @PutMapping("/asUser/cancel")
    public AjaxResult cancelAsUser(@RequestBody SurveyUserQuestionnaire userQuestionnaire)
    {
        return toAjax(surveyQuestionnaireService.deleteAsUser(userQuestionnaire));
    }

    @PutMapping("/asUser/cancelAll")
    public AjaxResult cancelAuthUserAll(String questionnaireId, String[] userIds)
    {
        return toAjax(surveyQuestionnaireService.deleteAsUsers(questionnaireId, userIds));
    }

    @PutMapping("/saveQuestionCount")
    public AjaxResult saveQuestionCount(String questionnaireId, String questionCount){
        return toAjax(surveyQuestionnaireService.updateSurveyQuestionCount(questionnaireId,questionCount));
    }

}
