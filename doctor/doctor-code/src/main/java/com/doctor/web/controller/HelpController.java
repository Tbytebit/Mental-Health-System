package com.doctor.web.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.doctor.web.domain.Help;
import com.doctor.web.service.IHelpService;
import com.doctor.common.core.domain.R;
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
 * 帮助内容Controller
 * 
 * @author LI
 * @date 2024-05-09
 */
@RestController
@RequestMapping("/maincode/help")
public class HelpController extends BaseController
{
    @Autowired
    private IHelpService helpService;

    /**
     * 查询帮助内容列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:help:list')")
    @GetMapping("/list")
    public TableDataInfo list(Help help)
    {
        startPage();
        List<Help> list = helpService.selectHelpList(help);
        return getDataTable(list);
    }

    /**
     * 导出帮助内容列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:help:export')")
    @Log(title = "帮助内容", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Help help)
    {
        List<Help> list = helpService.selectHelpList(help);
        ExcelUtil<Help> util = new ExcelUtil<Help>(Help.class);
        util.exportExcel(response, list, "帮助内容数据");
    }

    /**
     * 获取帮助内容详细信息
     */
    @PreAuthorize("@ss.hasPermi('maincode:help:query')")
    @GetMapping(value = "/{helpId}")
    public AjaxResult getInfo(@PathVariable("helpId") Long helpId)
    {
        return success(helpService.selectHelpByHelpId(helpId));
    }

    /**
     * 新增帮助内容
     */
    @PreAuthorize("@ss.hasPermi('maincode:help:add')")
    @Log(title = "帮助内容", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Help help)
    {
        return toAjax(helpService.insertHelp(help));
    }

    /**
     * 修改帮助内容
     */
    @PreAuthorize("@ss.hasPermi('maincode:help:edit')")
    @Log(title = "帮助内容", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Help help)
    {
        return toAjax(helpService.updateHelp(help));
    }

    /**
     * 删除帮助内容
     */
    @PreAuthorize("@ss.hasPermi('maincode:help:remove')")
    @Log(title = "帮助内容", businessType = BusinessType.DELETE)
	@DeleteMapping("/{helpIds}")
    public AjaxResult remove(@PathVariable Long[] helpIds)
    {
        return toAjax(helpService.deleteHelpByHelpIds(helpIds));
    }



    /**
     * 查询帮助内容列表
     */
    @GetMapping("/getHelpList")
    public R<List<Help>> getHelpList(Help help) {
        return R.ok(helpService.selectHelpList(help));
    }

}
