package com.doctor.web.controller;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.doctor.web.domain.Feedback;
import com.doctor.web.service.IFeedbackService;
import com.doctor.common.core.domain.R;
import com.doctor.common.utils.SecurityUtils;
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
import com.doctor.common.utils.poi.ExcelUtil;
import com.doctor.common.core.page.TableDataInfo;

/**
 * 用户反馈Controller
 *
 * @author LI
 * @date 2024-05-09
 */
@RestController
@RequestMapping("/maincode/feedback")
public class FeedbackController extends BaseController {
    @Autowired
    private IFeedbackService feedbackService;

    /**
     * 查询用户反馈列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:feedback:list')")
    @GetMapping("/list")
    public TableDataInfo list(Feedback feedback) {
        startPage();
        List<Feedback> list = feedbackService.selectFeedbackList(feedback);
        return getDataTable(list);
    }

    /**
     * 导出用户反馈列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:feedback:export')")
    @Log(title = "用户反馈", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Feedback feedback) {
        List<Feedback> list = feedbackService.selectFeedbackList(feedback);
        ExcelUtil<Feedback> util = new ExcelUtil<Feedback>(Feedback.class);
        util.exportExcel(response, list, "用户反馈数据");
    }

    /**
     * 获取用户反馈详细信息
     */
    @PreAuthorize("@ss.hasPermi('maincode:feedback:query')")
    @GetMapping(value = "/{feedbackId}")
    public AjaxResult getInfo(@PathVariable("feedbackId") Long feedbackId) {
        return success(feedbackService.selectFeedbackByFeedbackId(feedbackId));
    }

    /**
     * 新增用户反馈
     */
    @PreAuthorize("@ss.hasPermi('maincode:feedback:add')")
    @Log(title = "用户反馈", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Feedback feedback) {
//        feedback.setUserId(SecurityUtils.getUserId());
        feedback.setCreateBy(SecurityUtils.getUserId().toString());
        feedback.setCreateTime(new Date());
        return toAjax(feedbackService.insertFeedback(feedback));
    }

    /**
     * 修改用户反馈
     */
    @PreAuthorize("@ss.hasPermi('maincode:feedback:edit')")
    @Log(title = "用户反馈", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Feedback feedback) {
        feedback.setUpdateBy(SecurityUtils.getUserId().toString());
        return toAjax(feedbackService.updateFeedback(feedback));
    }

    /**
     * 删除用户反馈
     */
    @PreAuthorize("@ss.hasPermi('maincode:feedback:remove')")
    @Log(title = "用户反馈", businessType = BusinessType.DELETE)
    @DeleteMapping("/{feedbackIds}")
    public AjaxResult remove(@PathVariable Long[] feedbackIds) {
        return toAjax(feedbackService.deleteFeedbackByFeedbackIds(feedbackIds));
    }

    /**
     * 用户反馈
     *
     * @param feedback
     * @return
     */
    @Log(title = "用户反馈", businessType = BusinessType.INSERT)
    @PostMapping("/userFeedback")
    public R userFeedback(@RequestBody Feedback feedback) {
        Integer num = feedbackService.countDay(SecurityUtils.getUserId());
        if (num >= 3) {
            return R.fail("今天反馈次数已经到3次了");
        }
        feedback.setUserId(SecurityUtils.getUserId());
        feedback.setCreateBy(SecurityUtils.getUserId().toString());
        feedback.setFeedbackTime(new Date());
        int i = feedbackService.insertFeedback(feedback);
        if (i > 0) {
            return R.ok("反馈意见已经提交");
        }
        return R.fail("反馈提交失败");
    }

    @Log(title = "用户反馈", businessType = BusinessType.INSERT)
    @PostMapping("/addFeedback")
    public AjaxResult addFeedback(@RequestBody Feedback feedback) {
        feedback.setUserId(SecurityUtils.getUserId());
        feedback.setFeedbackTime(new Date());
        feedback.setCreateBy(SecurityUtils.getUserId().toString());
        feedback.setCreateTime(new Date());
        return toAjax(feedbackService.insertFeedback(feedback));
    }

    @GetMapping("/getFeedbackList")
    public TableDataInfo getFeedbackList(Feedback feedback) {
        feedback.setUserId(SecurityUtils.getUserId());
        startPage();
        List<Feedback> list = feedbackService.selectFeedbackList(feedback);
        return getDataTable(list);
    }


}
