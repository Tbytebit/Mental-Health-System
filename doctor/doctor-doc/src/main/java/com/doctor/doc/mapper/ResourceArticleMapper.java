package com.doctor.doc.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.doctor.doc.domain.ResourceArticle;
import com.doctor.doc.domain.dto.ResourceArticleDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 心理资料文章Mapper接口
 */
@Mapper
public interface ResourceArticleMapper extends BaseMapper<ResourceArticle> {
    
    /**
     * 分页查询文章列表
     * 
     * @param page 分页信息
     * @param categoryId 分类ID
     * @return 文章DTO列表
     */
    IPage<ResourceArticleDto> selectArticlePage(Page<ResourceArticleDto> page, @Param("categoryId") Long categoryId);
    
    /**
     * 获取推荐文章列表
     * 
     * @param limit 限制数量
     * @return 推荐文章DTO列表
     */
    List<ResourceArticleDto> selectFeaturedList(@Param("limit") Integer limit);
    
    /**
     * 获取相关文章
     * 
     * @param articleId 当前文章ID
     * @param categoryId 分类ID
     * @param limit 限制数量
     * @return 相关文章DTO列表
     */
    List<ResourceArticleDto> selectRelatedList(@Param("articleId") Long articleId, 
                                              @Param("categoryId") Long categoryId, 
                                              @Param("limit") Integer limit);
    
    /**
     * 根据ID获取文章详情
     * 
     * @param id 文章ID
     * @return 文章DTO对象
     */
    ResourceArticleDto selectArticleById(@Param("id") Long id);
    
    /**
     * 增加浏览量
     * 
     * @param id 文章ID
     * @return 影响行数
     */
    int incrementViewCount(@Param("id") Long id);
} 