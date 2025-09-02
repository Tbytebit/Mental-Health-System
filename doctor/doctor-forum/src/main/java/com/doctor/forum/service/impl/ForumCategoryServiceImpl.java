package com.doctor.forum.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doctor.forum.domain.entity.ForumCategory;
import com.doctor.forum.mapper.ForumCategoryMapper;
import com.doctor.forum.service.IForumCategoryService;

/**
 * 论坛分类Service实现类
 */
@Service
public class ForumCategoryServiceImpl implements IForumCategoryService {
    
    @Autowired
    private ForumCategoryMapper forumCategoryMapper;
    
    /**
     * 查询分类列表
     * 
     * @param forumCategory 查询条件
     * @return 分类列表
     */
    @Override
    public List<ForumCategory> selectForumCategoryList(ForumCategory forumCategory) {
        return forumCategoryMapper.selectForumCategoryList(forumCategory);
    }
    
    /**
     * 查询所有分类
     * 
     * @return 分类列表
     */
    @Override
    public List<ForumCategory> selectForumCategoryAll() {
        return forumCategoryMapper.selectForumCategoryAll();
    }
    
    /**
     * 查询分类详情
     * 
     * @param id 分类ID
     * @return 分类详情
     */
    @Override
    public ForumCategory selectForumCategoryById(String id) {
        return forumCategoryMapper.selectForumCategoryById(id);
    }
    
    /**
     * 新增分类
     * 
     * @param forumCategory 分类信息
     * @return 结果
     */
    @Override
    public int insertForumCategory(ForumCategory forumCategory) {
        // 设置默认值
        if (forumCategory.getOrderNum() == null) {
            forumCategory.setOrderNum(1);
        }
        forumCategory.setDelFlag("0");
        
        return forumCategoryMapper.insertForumCategory(forumCategory);
    }
    
    /**
     * 修改分类
     * 
     * @param forumCategory 分类信息
     * @return 结果
     */
    @Override
    public int updateForumCategory(ForumCategory forumCategory) {
        return forumCategoryMapper.updateForumCategory(forumCategory);
    }
    
    /**
     * 删除分类
     * 
     * @param id 分类ID
     * @return 结果
     */
    @Override
    public int deleteForumCategoryById(String id) {
        return forumCategoryMapper.deleteForumCategoryById(id);
    }
    
    /**
     * 批量删除分类
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteForumCategoryByIds(String[] ids) {
        return forumCategoryMapper.deleteForumCategoryByIds(ids);
    }
} 