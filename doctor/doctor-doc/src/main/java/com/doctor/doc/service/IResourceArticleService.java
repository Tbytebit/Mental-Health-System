package com.doctor.doc.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.doctor.doc.domain.ResourceArticle;
import com.doctor.doc.domain.dto.ResourceArticleDto;

import java.util.List;

/**
 * 心理资料文章Service接口
 */
public interface IResourceArticleService extends IService<ResourceArticle> {
    
    /**
     * 分页查询文章列表
     * 
     * @param pageNum 页码
     * @param pageSize 每页大小
     * @param categoryId 分类ID
     * @return 分页数据
     */
    IPage<ResourceArticleDto> getArticlePage(Integer pageNum, Integer pageSize, Long categoryId);
    
    /**
     * 获取推荐文章列表
     * 
     * @param limit 限制数量
     * @return 推荐文章列表
     */
    List<ResourceArticleDto> getFeaturedList(Integer limit);
    
    /**
     * 获取相关文章
     * 
     * @param articleId 当前文章ID
     * @param categoryId 分类ID
     * @param limit 限制数量
     * @return 相关文章列表
     */
    List<ResourceArticleDto> getRelatedList(Long articleId, Long categoryId, Integer limit);
    
    /**
     * 根据ID获取文章详情
     * 
     * @param id 文章ID
     * @return 文章详情
     */
    ResourceArticleDto getArticleById(Long id);
    
    /**
     * 增加浏览量
     * 
     * @param id 文章ID
     * @return 成功标志
     */
    boolean incrementViewCount(Long id);
} 