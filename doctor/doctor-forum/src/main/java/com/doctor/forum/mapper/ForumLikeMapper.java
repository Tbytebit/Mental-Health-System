package com.doctor.forum.mapper;

import java.util.List;
import com.doctor.forum.domain.entity.ForumLike;

/**
 * 论坛点赞Mapper接口
 */
public interface ForumLikeMapper {
    
    /**
     * 查询点赞列表
     * 
     * @param forumLike 查询条件
     * @return 点赞列表
     */
    public List<ForumLike> selectForumLikeList(ForumLike forumLike);
    
    /**
     * 查询点赞详情
     * 
     * @param id 点赞ID
     * @return 点赞详情
     */
    public ForumLike selectForumLikeById(Long id);
    
    /**
     * 查询用户是否点赞过
     * 
     * @param forumLike 点赞信息
     * @return 点赞记录
     */
    public ForumLike selectForumLikeByTypeAndTargetIdAndUserId(ForumLike forumLike);
    
    /**
     * 新增点赞
     * 
     * @param forumLike 点赞信息
     * @return 结果
     */
    public int insertForumLike(ForumLike forumLike);
    
    /**
     * 修改点赞
     * 
     * @param forumLike 点赞信息
     * @return 结果
     */
    public int updateForumLike(ForumLike forumLike);
    
    /**
     * 删除点赞
     * 
     * @param id 点赞ID
     * @return 结果
     */
    public int deleteForumLikeById(Long id);
    
    /**
     * 批量删除点赞
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteForumLikeByIds(Long[] ids);
    
    /**
     * 取消点赞
     * 
     * @param forumLike 点赞信息
     * @return 结果
     */
    public int deleteForumLikeByTypeAndTargetIdAndUserId(ForumLike forumLike);
} 