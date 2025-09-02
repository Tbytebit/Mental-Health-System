package com.doctor.web.mapper;

import java.util.List;
import com.doctor.web.domain.NewsArticles;

/**
 * 新闻管理Mapper接口
 * 
 * @author Li
 * @date 2024-05-12
 */
public interface NewsArticlesMapper 
{
    /**
     * 查询新闻管理
     * 
     * @param articleId 新闻管理主键
     * @return 新闻管理
     */
    public NewsArticles selectNewsArticlesByArticleId(Long articleId);

    /**
     * 查询新闻管理列表
     * 
     * @param newsArticles 新闻管理
     * @return 新闻管理集合
     */
    public List<NewsArticles> selectNewsArticlesList(NewsArticles newsArticles);

    /**
     * 新增新闻管理
     * 
     * @param newsArticles 新闻管理
     * @return 结果
     */
    public int insertNewsArticles(NewsArticles newsArticles);

    /**
     * 修改新闻管理
     * 
     * @param newsArticles 新闻管理
     * @return 结果
     */
    public int updateNewsArticles(NewsArticles newsArticles);

    /**
     * 删除新闻管理
     * 
     * @param articleId 新闻管理主键
     * @return 结果
     */
    public int deleteNewsArticlesByArticleId(Long articleId);

    /**
     * 批量删除新闻管理
     * 
     * @param articleIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteNewsArticlesByArticleIds(Long[] articleIds);
}
