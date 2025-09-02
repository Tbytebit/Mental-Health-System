package com.doctor.forum.mapper;

import java.util.List;
import com.doctor.forum.domain.entity.ForumPost;

/**
 * 论坛帖子Mapper接口
 */
public interface ForumPostMapper {
    
    /**
     * 查询论坛帖子列表
     * 
     * @param forumPost 查询条件
     * @return 帖子列表
     */
    public List<ForumPost> selectForumPostList(ForumPost forumPost);
    
    /**
     * 查询论坛帖子详情
     * 
     * @param id 帖子ID
     * @return 帖子详情
     */
    public ForumPost selectForumPostById(Long id);
    
    /**
     * 新增论坛帖子
     * 
     * @param forumPost 帖子信息
     * @return 结果
     */
    public int insertForumPost(ForumPost forumPost);
    
    /**
     * 修改论坛帖子
     * 
     * @param forumPost 帖子信息
     * @return 结果
     */
    public int updateForumPost(ForumPost forumPost);
    
    /**
     * 删除论坛帖子
     * 
     * @param id 帖子ID
     * @return 结果
     */
    public int deleteForumPostById(Long id);
    
    /**
     * 批量删除论坛帖子
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteForumPostByIds(Long[] ids);
    
    /**
     * 增加浏览量
     * 
     * @param id 帖子ID
     * @return 结果
     */
    public int increaseViewCount(Long id);
    
    /**
     * 增加点赞量
     * 
     * @param id 帖子ID
     * @return 结果
     */
    public int increaseLikeCount(Long id);
    
    /**
     * 减少点赞量
     * 
     * @param id 帖子ID
     * @return 结果
     */
    public int decreaseLikeCount(Long id);
    
    /**
     * 增加评论量
     * 
     * @param id 帖子ID
     * @return 结果
     */
    public int increaseCommentCount(Long id);
    
    /**
     * 统计符合条件的帖子数量
     * 
     * @param forumPost 帖子信息
     * @return 结果
     */
    public int countForumPost(ForumPost forumPost);
} 