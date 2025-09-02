package com.doctor.forum.service;

import java.util.List;
import com.doctor.forum.domain.entity.ForumCategory;

/**
 * 论坛分类Service接口
 */
public interface IForumCategoryService {
    
    /**
     * 查询分类列表
     * 
     * @param forumCategory 查询条件
     * @return 分类列表
     */
    public List<ForumCategory> selectForumCategoryList(ForumCategory forumCategory);
    
    /**
     * 查询所有分类
     * 
     * @return 分类列表
     */
    public List<ForumCategory> selectForumCategoryAll();
    
    /**
     * 查询分类详情
     * 
     * @param id 分类ID
     * @return 分类详情
     */
    public ForumCategory selectForumCategoryById(String id);
    
    /**
     * 新增分类
     * 
     * @param forumCategory 分类信息
     * @return 结果
     */
    public int insertForumCategory(ForumCategory forumCategory);
    
    /**
     * 修改分类
     * 
     * @param forumCategory 分类信息
     * @return 结果
     */
    public int updateForumCategory(ForumCategory forumCategory);
    
    /**
     * 删除分类
     * 
     * @param id 分类ID
     * @return 结果
     */
    public int deleteForumCategoryById(String id);
    
    /**
     * 批量删除分类
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteForumCategoryByIds(String[] ids);
} 