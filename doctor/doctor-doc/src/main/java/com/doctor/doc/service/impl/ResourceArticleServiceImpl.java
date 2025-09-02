package com.doctor.doc.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.doctor.doc.domain.ResourceArticle;
import com.doctor.doc.domain.dto.ResourceArticleDto;
import com.doctor.doc.mapper.ResourceArticleMapper;
import com.doctor.doc.service.IResourceArticleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 心理资料文章Service实现类
 */
@Service
public class ResourceArticleServiceImpl extends ServiceImpl<ResourceArticleMapper, ResourceArticle> implements IResourceArticleService {
    
    private static final Logger log = LoggerFactory.getLogger(ResourceArticleServiceImpl.class);
    
    @Override
    public IPage<ResourceArticleDto> getArticlePage(Integer pageNum, Integer pageSize, Long categoryId) {
        Page<ResourceArticleDto> page = new Page<>(pageNum, pageSize);
        try {
            return baseMapper.selectArticlePage(page, categoryId);
        } catch (org.apache.ibatis.exceptions.TooManyResultsException e) {
            log.error("获取文章列表时出现多条记录异常: pageNum={}, pageSize={}, categoryId={}", pageNum, pageSize, categoryId, e);
            // 返回空页面结果
            return new Page<>();
        } catch (Exception e) {
            log.error("获取文章列表时出现异常: pageNum={}, pageSize={}, categoryId={}", pageNum, pageSize, categoryId, e);
            return new Page<>();
        }
    }
    
    @Override
    public List<ResourceArticleDto> getFeaturedList(Integer limit) {
        try {
            return baseMapper.selectFeaturedList(limit);
        } catch (Exception e) {
            log.error("获取推荐文章列表时出现异常: ", e);
            return java.util.Collections.emptyList();
        }
    }
    
    @Override
    public List<ResourceArticleDto> getRelatedList(Long articleId, Long categoryId, Integer limit) {
        try {
            return baseMapper.selectRelatedList(articleId, categoryId, limit);
        } catch (Exception e) {
            log.error("获取相关文章列表时出现异常: articleId={}, categoryId={}", articleId, categoryId, e);
            return java.util.Collections.emptyList();
        }
    }
    
    @Override
    public ResourceArticleDto getArticleById(Long id) {
        if (id == null) {
            return null;
        }
        try {
            return baseMapper.selectArticleById(id);
        } catch (org.apache.ibatis.exceptions.TooManyResultsException e) {
            log.error("获取文章详情时出现多条记录异常: id={}", id, e);
            // 尝试获取第一条记录作为替代方案
            try {
                List<ResourceArticleDto> list = baseMapper.selectRelatedList(id, null, 1);
                if (list != null && !list.isEmpty()) {
                    log.info("获取文章详情时出现多条记录，已返回第一条: id={}", id);
                    return list.get(0);
                }
            } catch (Exception ex) {
                log.error("尝试获取第一条文章记录时出现异常: id={}", id, ex);
            }
            return null;
        }
    }
    
    @Override
    public boolean incrementViewCount(Long id) {
        try {
            return baseMapper.incrementViewCount(id) > 0;
        } catch (Exception e) {
            log.error("增加文章浏览量时出现异常: id={}", id, e);
            return false;
        }
    }
}