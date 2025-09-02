package com.doctor.web.service.impl;

import java.util.List;
import com.doctor.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doctor.web.mapper.NewsArticlesMapper;
import com.doctor.web.domain.NewsArticles;
import com.doctor.web.service.INewsArticlesService;

/**
 * 新闻管理Service业务层处理
 * 
 * @author Li
 * @date 2024-05-12
 */
@Service
public class NewsArticlesServiceImpl implements INewsArticlesService 
{
    @Autowired
    private NewsArticlesMapper newsArticlesMapper;

    /**
     * 查询新闻管理
     * 
     * @param articleId 新闻管理主键
     * @return 新闻管理
     */
    @Override
    public NewsArticles selectNewsArticlesByArticleId(Long articleId)
    {
        return newsArticlesMapper.selectNewsArticlesByArticleId(articleId);
    }

    /**
     * 查询新闻管理列表
     * 
     * @param newsArticles 新闻管理
     * @return 新闻管理
     */
    @Override
    public List<NewsArticles> selectNewsArticlesList(NewsArticles newsArticles)
    {
        return newsArticlesMapper.selectNewsArticlesList(newsArticles);
    }

    /**
     * 新增新闻管理
     * 
     * @param newsArticles 新闻管理
     * @return 结果
     */
    @Override
    public int insertNewsArticles(NewsArticles newsArticles)
    {
        newsArticles.setCreateTime(DateUtils.getNowDate());
        return newsArticlesMapper.insertNewsArticles(newsArticles);
    }

    /**
     * 修改新闻管理
     * 
     * @param newsArticles 新闻管理
     * @return 结果
     */
    @Override
    public int updateNewsArticles(NewsArticles newsArticles)
    {
        newsArticles.setUpdateTime(DateUtils.getNowDate());
        return newsArticlesMapper.updateNewsArticles(newsArticles);
    }

    /**
     * 批量删除新闻管理
     * 
     * @param articleIds 需要删除的新闻管理主键
     * @return 结果
     */
    @Override
    public int deleteNewsArticlesByArticleIds(Long[] articleIds)
    {
        return newsArticlesMapper.deleteNewsArticlesByArticleIds(articleIds);
    }

    /**
     * 删除新闻管理信息
     * 
     * @param articleId 新闻管理主键
     * @return 结果
     */
    @Override
    public int deleteNewsArticlesByArticleId(Long articleId)
    {
        return newsArticlesMapper.deleteNewsArticlesByArticleId(articleId);
    }
}
