package com.doctor.web.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import com.doctor.web.domain.Advertisements;
import com.doctor.web.service.IAdvertisementsService;
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
 * 广告Controller
 * 
 * @author li
 * @date 2024-04-10
 */
@RestController
@RequestMapping("/maincode/advertisements")
public class AdvertisementsController extends BaseController
{
    @Autowired
    private IAdvertisementsService advertisementsService;

    /**
     * 查询广告列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:advertisements:list')")
    @GetMapping("/list")
    public TableDataInfo list(Advertisements advertisements)
    {
        startPage();
        List<Advertisements> list = advertisementsService.selectAdvertisementsList(advertisements);
        return getDataTable(list);
    }

    /**
     * 导出广告列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:advertisements:export')")
    @Log(title = "广告", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Advertisements advertisements)
    {
        List<Advertisements> list = advertisementsService.selectAdvertisementsList(advertisements);
        ExcelUtil<Advertisements> util = new ExcelUtil<Advertisements>(Advertisements.class);
        util.exportExcel(response, list, "广告数据");
    }

    /**
     * 获取广告详细信息
     */
    @PreAuthorize("@ss.hasPermi('maincode:advertisements:query')")
    @GetMapping(value = "/{adId}")
    public AjaxResult getInfo(@PathVariable("adId") Long adId)
    {
        return success(advertisementsService.selectAdvertisementsByAdId(adId));
    }

    /**
     * 新增广告
     */
    @PreAuthorize("@ss.hasPermi('maincode:advertisements:add')")
    @Log(title = "广告", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Advertisements advertisements)
    {
        advertisements.setCreateBy(SecurityUtils.getUserId().toString());
        return toAjax(advertisementsService.insertAdvertisements(advertisements));
    }

    /**
     * 修改广告
     */
    @PreAuthorize("@ss.hasPermi('maincode:advertisements:edit')")
    @Log(title = "广告", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Advertisements advertisements)
    {
        advertisements.setUpdateBy(SecurityUtils.getUserId().toString());
        return toAjax(advertisementsService.updateAdvertisements(advertisements));
    }

    /**
     * 删除广告
     */
    @PreAuthorize("@ss.hasPermi('maincode:advertisements:remove')")
    @Log(title = "广告", businessType = BusinessType.DELETE)
	@DeleteMapping("/{adIds}")
    public AjaxResult remove(@PathVariable Long[] adIds)
    {
        return toAjax(advertisementsService.deleteAdvertisementsByAdIds(adIds));
    }

    /**
     * 获取广告轮播
     */
    @GetMapping("/getAdCarousel")
    public R<List<Advertisements>> getAdCarousel() {
        return R.ok(advertisementsService.getAdCarousel());
    }


    @GetMapping(value = "/getAdvertisement/{adId}")
    public AjaxResult getAdvertisement(@PathVariable("adId") Long adId)
    {
        return success(advertisementsService.selectAdvertisementsByAdId(adId));
    }
}
