package com.doctor.web.controller;

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
import com.doctor.web.domain.NewsArticles;
import com.doctor.web.service.INewsArticlesService;
import com.doctor.common.utils.poi.ExcelUtil;
import com.doctor.common.core.page.TableDataInfo;

/**
 * 新闻管理Controller
 * 
 * @author Li
 * @date 2024-05-12
 */
@RestController
@RequestMapping("/maincode/articles")
public class NewsArticlesController extends BaseController
{
    @Autowired
    private INewsArticlesService newsArticlesService;

    /**
     * 查询新闻管理列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:articles:list')")
    @GetMapping("/list")
    public TableDataInfo list(NewsArticles newsArticles)
    {
        startPage();
        List<NewsArticles> list = newsArticlesService.selectNewsArticlesList(newsArticles);
        return getDataTable(list);
    }

    /**
     * 导出新闻管理列表
     */
    @PreAuthorize("@ss.hasPermi('maincode:articles:export')")
    @Log(title = "新闻管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, NewsArticles newsArticles)
    {
        List<NewsArticles> list = newsArticlesService.selectNewsArticlesList(newsArticles);
        ExcelUtil<NewsArticles> util = new ExcelUtil<NewsArticles>(NewsArticles.class);
        util.exportExcel(response, list, "新闻管理数据");
    }

    /**
     * 获取新闻管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('maincode:articles:query')")
    @GetMapping(value = "/{articleId}")
    public AjaxResult getInfo(@PathVariable("articleId") Long articleId)
    {
        return success(newsArticlesService.selectNewsArticlesByArticleId(articleId));
    }

    /**
     * 新增新闻管理
     */
    @PreAuthorize("@ss.hasPermi('maincode:articles:add')")
    @Log(title = "新闻管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody NewsArticles newsArticles)
    {
        return toAjax(newsArticlesService.insertNewsArticles(newsArticles));
    }

    /**
     * 修改新闻管理
     */
    @PreAuthorize("@ss.hasPermi('maincode:articles:edit')")
    @Log(title = "新闻管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody NewsArticles newsArticles)
    {
        return toAjax(newsArticlesService.updateNewsArticles(newsArticles));
    }

    /**
     * 删除新闻管理
     */
    @PreAuthorize("@ss.hasPermi('maincode:articles:remove')")
    @Log(title = "新闻管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{articleIds}")
    public AjaxResult remove(@PathVariable Long[] articleIds)
    {
        return toAjax(newsArticlesService.deleteNewsArticlesByArticleIds(articleIds));
    }

    @GetMapping("/getNewsArticlesList")
    public TableDataInfo getNewsArticlesList(NewsArticles newsArticles)
    {
        startPage();
        List<NewsArticles> list = newsArticlesService.selectNewsArticlesList(newsArticles);
        return getDataTable(list);
    }

    /**
     * 获取新闻管理详细信息
     */
    @GetMapping(value = "/getArticleInfo/{articleId}")
    public AjaxResult getArticleInfo(@PathVariable("articleId") Long articleId)  {
        return success(newsArticlesService.selectNewsArticlesByArticleId(articleId));
    }



}
