package com.doctor.forum.mapper;

import java.util.List;
import com.doctor.forum.domain.entity.ForumComment;

/**
 * 论坛评论Mapper接口
 */
public interface ForumCommentMapper {
    
    /**
     * 查询评论列表
     * 
     * @param forumComment 查询条件
     * @return 评论列表
     */
    public List<ForumComment> selectForumCommentList(ForumComment forumComment);
    
    /**
     * 查询帖子的评论列表
     * 
     * @param postId 帖子ID
     * @return 评论列表
     */
    public List<ForumComment> selectForumCommentByPostId(Long postId);
    
    /**
     * 查询评论详情
     * 
     * @param id 评论ID
     * @return 评论详情
     */
    public ForumComment selectForumCommentById(Long id);
    
    /**
     * 新增评论
     * 
     * @param forumComment 评论信息
     * @return 结果
     */
    public int insertForumComment(ForumComment forumComment);
    
    /**
     * 修改评论
     * 
     * @param forumComment 评论信息
     * @return 结果
     */
    public int updateForumComment(ForumComment forumComment);
    
    /**
     * 删除评论
     * 
     * @param id 评论ID
     * @return 结果
     */
    public int deleteForumCommentById(Long id);
    
    /**
     * 批量删除评论
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteForumCommentByIds(Long[] ids);
    
    /**
     * 根据帖子ID删除评论
     * 
     * @param postId 帖子ID
     * @return 结果
     */
    public int deleteForumCommentByPostId(Long postId);
    
    /**
     * 增加点赞量
     * 
     * @param id 评论ID
     * @return 结果
     */
    public int increaseLikeCount(Long id);
    
    /**
     * 减少点赞量
     * 
     * @param id 评论ID
     * @return 结果
     */
    public int decreaseLikeCount(Long id);
} 