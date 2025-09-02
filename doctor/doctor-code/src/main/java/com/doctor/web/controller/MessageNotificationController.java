package com.doctor.web.controller;

import java.util.Date;
import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.doctor.web.domain.MessageNotification;
import com.doctor.web.service.IMessageNotificationService;
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
 * 消息通知Controller
 * 
 * @author LI
 * @date 2024-05-09
 */
@RestController
@RequestMapping("/maincode/notification")
public class MessageNotificationController extends BaseController
{
    @Autowired
    private IMessageNotificationService messageNotificationService;

    /**
     * 查询消息通知列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:notification:list')")
    @GetMapping("/list")
    public TableDataInfo list(MessageNotification messageNotification)
    {
        startPage();
        List<MessageNotification> list = messageNotificationService.selectMessageNotificationList(messageNotification);
        return getDataTable(list);
    }

    /**
     * 导出消息通知列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:notification:export')")
    @Log(title = "消息通知", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MessageNotification messageNotification)
    {
        List<MessageNotification> list = messageNotificationService.selectMessageNotificationList(messageNotification);
        ExcelUtil<MessageNotification> util = new ExcelUtil<MessageNotification>(MessageNotification.class);
        util.exportExcel(response, list, "消息通知数据");
    }

    /**
     * 获取消息通知详细信息
     */
    @PreAuthorize("@ss.hasPermi('maincode:notification:query')")
    @GetMapping(value = "/{messageId}")
    public AjaxResult getInfo(@PathVariable("messageId") Long messageId)
    {
        return success(messageNotificationService.selectMessageNotificationByMessageId(messageId));
    }

    /**
     * 新增消息通知
     */
    @PreAuthorize("@ss.hasPermi('maincode:notification:add')")
    @Log(title = "消息通知", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MessageNotification messageNotification)
    {
        messageNotification.setCreateBy(SecurityUtils.getUserId().toString());
        messageNotification.setSendTime(new Date());
        messageNotification.setSenderUserId(SecurityUtils.getUserId());
        return toAjax(messageNotificationService.insertMessageNotification(messageNotification));
    }

    /**
     * 修改消息通知
     */
    @PreAuthorize("@ss.hasPermi('maincode:notification:edit')")
    @Log(title = "消息通知", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MessageNotification messageNotification)
    {
        messageNotification.setUpdateBy(SecurityUtils.getUserId().toString());
        return toAjax(messageNotificationService.updateMessageNotification(messageNotification));
    }

    /**
     * 删除消息通知
     */
    @PreAuthorize("@ss.hasPermi('maincode:notification:remove')")
    @Log(title = "消息通知", businessType = BusinessType.DELETE)
	@DeleteMapping("/{messageIds}")
    public AjaxResult remove(@PathVariable Long[] messageIds)
    {
        return toAjax(messageNotificationService.deleteMessageNotificationByMessageIds(messageIds));
    }


    @GetMapping("/getUserMsgList")
    public TableDataInfo getUserMsgList(MessageNotification messageNotification) {
        startPage();
        messageNotification.setRecipientUserId(SecurityUtils.getUserId());
        List<MessageNotification> list = messageNotificationService.getUserMsgList(messageNotification);
        return getDataTable(list);
    }


}
