package com.doctor.doc.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.doctor.doc.domain.ResourceCategory;

import java.util.List;

/**
 * 心理资料分类Service接口
 */
public interface IResourceCategoryService extends IService<ResourceCategory> {
    
    /**
     * 获取分类列表
     * 
     * @return 分类列表
     */
    List<ResourceCategory> getCategoryList();
} 