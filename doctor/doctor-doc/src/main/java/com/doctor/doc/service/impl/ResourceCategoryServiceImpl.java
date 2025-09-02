package com.doctor.doc.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doctor.doc.domain.ResourceCategory;
import com.doctor.doc.mapper.ResourceCategoryMapper;
import com.doctor.doc.service.IResourceCategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 心理资料分类Service实现类
 */
@Service
public class ResourceCategoryServiceImpl extends ServiceImpl<ResourceCategoryMapper, ResourceCategory> implements IResourceCategoryService {
    
    @Override
    public List<ResourceCategory> getCategoryList() {
        LambdaQueryWrapper<ResourceCategory> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(ResourceCategory::getOrderNum);
        return list(wrapper);
    }
} 