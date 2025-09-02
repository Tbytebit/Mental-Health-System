package com.doctor.doc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doctor.doc.domain.ResourceCategory;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 心理资料分类Mapper接口
 */
@Mapper
public interface ResourceCategoryMapper extends BaseMapper<ResourceCategory> {
    /**
     * 查询资源分类列表
     * 
     * @return 资源分类列表
     */
    List<ResourceCategory> selectList();
}