package com.doctor.forum.service;

import java.util.List;
import com.doctor.forum.domain.entity.ForumPost;
import com.doctor.forum.domain.dto.ForumPostDTO;
import com.doctor.forum.domain.vo.ForumPostVO;

/**
 * 论坛帖子Service接口
 */
public interface IForumPostService {
    
    /**
     * 查询论坛帖子列表
     * 
     * @param forumPost 查询条件
     * @return 帖子列表
     */
    public List<ForumPostVO> selectForumPostList(ForumPost forumPost);
    
    /**
     * 查询论坛帖子详情
     * 
     * @param id 帖子ID
     * @param userId 当前用户ID
     * @return 帖子详情
     */
    public ForumPostVO selectForumPostById(Long id, Long userId);
    
    /**
     * 新增论坛帖子
     * 
     * @param forumPostDTO 帖子信息
     * @return 结果
     */
    public ForumPost insertForumPost(ForumPostDTO forumPostDTO);
    
    /**
     * 修改论坛帖子
     * 
     * @param forumPostDTO 帖子信息
     * @return 结果
     */
    public int updateForumPost(ForumPostDTO forumPostDTO);
    
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
     * 点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    public int likeForumPost(Long postId, Long userId);
    
    /**
     * 取消点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    public int unlikeForumPost(Long postId, Long userId);
    
    /**
     * 增加浏览量
     * 
     * @param id 帖子ID
     * @return 结果
     */
    public int increaseViewCount(Long id);
    
    /**
     * 统计用户发布的帖子数量
     * 
     * @param userId 用户ID
     * @return 帖子数量
     */
    int countUserPosts(Long userId);
} 