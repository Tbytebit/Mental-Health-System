package com.doctor.doc.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.doctor.common.core.controller.BaseController;
import com.doctor.common.core.domain.AjaxResult;
import com.doctor.doc.domain.dto.ResourceArticleDto;
import com.doctor.doc.service.IResourceArticleService;
import com.doctor.doc.utils.OssUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 心理资料文章Controller
 */
@RestController
@RequestMapping("/resource/article")
public class ResourceArticleController extends BaseController {
    
    @Autowired
    private IResourceArticleService articleService;
    
    /**
     * 获取文章列表
     */
    @GetMapping("/list")
    public AjaxResult list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "categoryId", defaultValue = "0") Long categoryId) {
        
        IPage<ResourceArticleDto> page = articleService.getArticlePage(pageNum, pageSize, categoryId);
        
        // 构建分页结果
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("total", page.getTotal());
        resultMap.put("rows", page.getRecords());
        resultMap.put("pageNum", page.getCurrent());
        resultMap.put("pageSize", page.getSize());
        
        return AjaxResult.success(resultMap);
    }
    
    /**
     * 获取推荐文章列表
     */
    @GetMapping("/featured")
    public AjaxResult featured(@RequestParam(value = "limit", defaultValue = "3") Integer limit) {
        List<ResourceArticleDto> list = articleService.getFeaturedList(limit);
        return AjaxResult.success(list);
    }
    
    /**
     * 获取相关文章
     */
    @GetMapping("/related")
    public AjaxResult related(
            @RequestParam("articleId") Long articleId,
            @RequestParam("categoryId") Long categoryId,
            @RequestParam(value = "limit", defaultValue = "3") Integer limit) {
        
        List<ResourceArticleDto> list = articleService.getRelatedList(articleId, categoryId, limit);
        return AjaxResult.success(list);
    }
    
    /**
     * 获取文章详情
     */
    @GetMapping("/{id}")
    public AjaxResult detail(@PathVariable("id") Long id) {
        ResourceArticleDto article = articleService.getArticleById(id);
        if (article == null) {
            return AjaxResult.error("文章不存在");
        }
        return AjaxResult.success(article);
    }
    
    /**
     * 文章浏览
     */
    @PostMapping("/view")
    public AjaxResult view(@RequestBody ResourceArticleDto articleDto) {
        if (articleDto == null || articleDto.getId() == null) {
            return AjaxResult.error(400, "文章ID不能为空");
        }
        boolean result = articleService.incrementViewCount(articleDto.getId());
        return result ? AjaxResult.success() : AjaxResult.error("增加浏览量失败");
    }
    
    /**
     * 文件上传
     */
    @PostMapping("/upload")
    public AjaxResult upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return AjaxResult.error("上传文件不能为空");
        }
        
        // 文件上传到OSS
        String url = OssUtils.uploadFile(file, "resource");
        
        // 返回文件访问路径
        return AjaxResult.success("上传成功", url);
    }
}