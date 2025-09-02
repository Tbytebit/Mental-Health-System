package com.doctor.doc.controller;

import com.doctor.common.core.controller.BaseController;
import com.doctor.common.core.domain.AjaxResult;
import com.doctor.doc.domain.ResourceCategory;
import com.doctor.doc.service.IResourceCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 心理资料分类Controller
 */
@RestController
@RequestMapping("/resource/category")
public class ResourceCategoryController extends BaseController {
    
    @Autowired
    private IResourceCategoryService categoryService;
    
    /**
     * 获取分类列表
     */
    @GetMapping("/list")
    public AjaxResult list() {
        List<ResourceCategory> list = categoryService.getCategoryList();
        return AjaxResult.success(list);
    }
} 