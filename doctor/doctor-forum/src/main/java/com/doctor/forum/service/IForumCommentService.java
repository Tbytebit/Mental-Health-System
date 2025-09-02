package com.doctor.forum.service;

import java.util.List;
import com.doctor.forum.domain.entity.ForumComment;
import com.doctor.forum.domain.dto.ForumCommentDTO;
import com.doctor.forum.domain.vo.ForumCommentVO;

/**
 * 论坛评论Service接口
 */
public interface IForumCommentService {
    
    /**
     * 查询评论列表
     * 
     * @param postId 帖子ID
     * @param userId 当前用户ID
     * @return 评论列表
     */
    public List<ForumCommentVO> selectForumCommentList(Long postId, Long userId);
    
    /**
     * 查询评论详情
     * 
     * @param id 评论ID
     * @param userId 当前用户ID
     * @return 评论详情
     */
    public ForumCommentVO selectForumCommentById(Long id, Long userId);
    
    /**
     * 新增评论
     * 
     * @param forumCommentDTO 评论信息
     * @return 结果
     */
    public ForumComment insertForumComment(ForumCommentDTO forumCommentDTO);
    
    /**
     * 修改评论
     * 
     * @param forumCommentDTO 评论信息
     * @return 结果
     */
    public int updateForumComment(ForumCommentDTO forumCommentDTO);
    
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
     * 点赞评论
     * 
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 结果
     */
    public int likeForumComment(Long commentId, Long userId);
    
    /**
     * 取消点赞评论
     * 
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 结果
     */
    public int unlikeForumComment(Long commentId, Long userId);
} 