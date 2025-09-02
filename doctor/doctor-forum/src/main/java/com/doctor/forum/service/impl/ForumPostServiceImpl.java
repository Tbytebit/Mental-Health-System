package com.doctor.forum.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.doctor.common.utils.DateUtils;
import com.doctor.common.utils.SecurityUtils;
import com.doctor.common.utils.StringUtils;
import com.doctor.system.service.ISysUserService;
import com.doctor.forum.domain.entity.ForumPost;
import com.doctor.forum.domain.entity.ForumLike;
import com.doctor.forum.domain.entity.ForumCategory;
import com.doctor.forum.domain.dto.ForumPostDTO;
import com.doctor.forum.domain.vo.ForumPostVO;
import com.doctor.forum.mapper.ForumPostMapper;
import com.doctor.forum.mapper.ForumLikeMapper;
import com.doctor.forum.mapper.ForumCategoryMapper;
import com.doctor.forum.mapper.ForumCommentMapper;
import com.doctor.forum.service.IForumPostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

/**
 * 论坛帖子Service实现
 */
@Service
public class ForumPostServiceImpl implements IForumPostService {
    
    @Autowired
    private ForumPostMapper forumPostMapper;
    
    @Autowired
    private ForumLikeMapper forumLikeMapper;
    
    @Autowired
    private ForumCategoryMapper forumCategoryMapper;
    
    @Autowired
    private ForumCommentMapper forumCommentMapper;
    
    @Autowired
    private ISysUserService userService;
    
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * 查询帖子列表
     * 
     * @param forumPost 查询条件
     * @return 帖子列表
     */
    @Override
    public List<ForumPostVO> selectForumPostList(ForumPost forumPost) {
        List<ForumPost> forumPosts = forumPostMapper.selectForumPostList(forumPost);
        List<ForumPostVO> forumPostVOs = new ArrayList<>();
        
        Long userId = null;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            // 未登录状态，忽略
        }
        
        for (ForumPost post : forumPosts) {
            ForumPostVO postVO = convertToVO(post, userId);
            forumPostVOs.add(postVO);
        }
        
        return forumPostVOs;
    }
    
    /**
     * 查询帖子详情
     * 
     * @param id 帖子ID
     * @param userId 当前用户ID
     * @return 帖子详情
     */
    @Override
    public ForumPostVO selectForumPostById(Long id, Long userId) {
        ForumPost forumPost = forumPostMapper.selectForumPostById(id);
        if (forumPost == null) {
            return null;
        }
        
        // 增加浏览量
        forumPostMapper.increaseViewCount(id);
        
        return convertToVO(forumPost, userId);
    }
    
    /**
     * 新增帖子
     * 
     * @param forumPostDTO 帖子信息
     * @return 结果
     */
    @Override
    @Transactional
    public ForumPost insertForumPost(ForumPostDTO forumPostDTO) {
        ForumPost forumPost = new ForumPost();
        BeanUtils.copyProperties(forumPostDTO, forumPost);
        
        // 获取分类名称
        if (StringUtils.isNotEmpty(forumPostDTO.getCategoryId())) {
            ForumCategory category = forumCategoryMapper.selectForumCategoryById(forumPostDTO.getCategoryId());
            if (category != null) {
                forumPost.setCategoryName(category.getName());
            }
        }
        
        // 设置默认值
        if (forumPost.getUserId() == null) {
            try {
                forumPost.setUserId(SecurityUtils.getUserId());
                forumPost.setUsername(SecurityUtils.getUsername());
                forumPost.setAvatar(userService.selectUserById(forumPost.getUserId()).getAvatar());
            } catch (Exception e) {
                // 未登录状态，使用默认用户
                forumPost.setUserId(1L);
                forumPost.setUsername("匿名用户");
                forumPost.setAvatar("/static/images/profile.jpg");
            }
        } else {
            // 根据UserId获取用户名和头像
            if (StringUtils.isEmpty(forumPost.getUsername())) {
                try {
                    forumPost.setUsername(userService.selectUserById(forumPost.getUserId()).getNickName());
                    forumPost.setAvatar(userService.selectUserById(forumPost.getUserId()).getAvatar());
                } catch (Exception e) {
                    forumPost.setUsername("用户" + forumPost.getUserId());
                    forumPost.setAvatar("/static/images/profile.jpg");
                }
            }
        }
        
        // 处理图片
        if (forumPostDTO.getImages() != null && !forumPostDTO.getImages().isEmpty()) {
            try {
                forumPost.setImages(objectMapper.writeValueAsString(forumPostDTO.getImages()));
            } catch (Exception e) {
                // 转换失败，使用默认空字符串
                forumPost.setImages("[]");
            }
        } else {
            forumPost.setImages("[]");
        }
        
        // 设置初始值
        forumPost.setLikeCount(0);
        forumPost.setCommentCount(0);
        forumPost.setViewCount(0);
        forumPost.setDelFlag("0");
        
        forumPostMapper.insertForumPost(forumPost);
        
        return forumPost;
    }
    
    /**
     * 修改帖子
     * 
     * @param forumPostDTO 帖子信息
     * @return 结果
     */
    @Override
    public int updateForumPost(ForumPostDTO forumPostDTO) {
        ForumPost forumPost = new ForumPost();
        BeanUtils.copyProperties(forumPostDTO, forumPost);
        
        // 获取分类名称
        if (StringUtils.isNotEmpty(forumPostDTO.getCategoryId())) {
            ForumCategory category = forumCategoryMapper.selectForumCategoryById(forumPostDTO.getCategoryId());
            if (category != null) {
                forumPost.setCategoryName(category.getName());
            }
        }
        
        // 处理图片
        if (forumPostDTO.getImages() != null && !forumPostDTO.getImages().isEmpty()) {
            try {
                forumPost.setImages(objectMapper.writeValueAsString(forumPostDTO.getImages()));
            } catch (Exception e) {
                // 转换失败，忽略
            }
        }
        
        return forumPostMapper.updateForumPost(forumPost);
    }
    
    /**
     * 删除帖子
     * 
     * @param id 帖子ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteForumPostById(Long id) {
        // 删除帖子评论
        forumCommentMapper.deleteForumCommentByPostId(id);
        // 删除帖子
        return forumPostMapper.deleteForumPostById(id);
    }
    
    /**
     * 批量删除帖子
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    @Transactional
    public int deleteForumPostByIds(Long[] ids) {
        // 删除帖子评论
        for (Long id : ids) {
            forumCommentMapper.deleteForumCommentByPostId(id);
        }
        // 删除帖子
        return forumPostMapper.deleteForumPostByIds(ids);
    }
    
    /**
     * 点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int likeForumPost(Long postId, Long userId) {
        // 检查是否已点赞
        ForumLike forumLike = new ForumLike();
        forumLike.setType(1); // 1-帖子
        forumLike.setTargetId(postId);
        forumLike.setUserId(userId);
        
        ForumLike existLike = forumLikeMapper.selectForumLikeByTypeAndTargetIdAndUserId(forumLike);
        if (existLike != null) {
            // 已点赞，直接返回
            return 0;
        }
        
        // 增加点赞记录
        forumLikeMapper.insertForumLike(forumLike);
        
        // 更新帖子点赞数
        return forumPostMapper.increaseLikeCount(postId);
    }
    
    /**
     * 取消点赞帖子
     * 
     * @param postId 帖子ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int unlikeForumPost(Long postId, Long userId) {
        // 检查是否已点赞
        ForumLike forumLike = new ForumLike();
        forumLike.setType(1); // 1-帖子
        forumLike.setTargetId(postId);
        forumLike.setUserId(userId);
        
        ForumLike existLike = forumLikeMapper.selectForumLikeByTypeAndTargetIdAndUserId(forumLike);
        if (existLike == null) {
            // 未点赞，直接返回
            return 0;
        }
        
        // 删除点赞记录
        forumLikeMapper.deleteForumLikeByTypeAndTargetIdAndUserId(forumLike);
        
        // 更新帖子点赞数
        return forumPostMapper.decreaseLikeCount(postId);
    }
    
    /**
     * 增加浏览量
     * 
     * @param id 帖子ID
     * @return 结果
     */
    @Override
    public int increaseViewCount(Long id) {
        return forumPostMapper.increaseViewCount(id);
    }
    
    /**
     * 统计用户发布的帖子数量
     * 
     * @param userId 用户ID
     * @return 帖子数量
     */
    @Override
    public int countUserPosts(Long userId) {
        if (userId == null) {
            return 0;
        }
        
        // 构建查询条件
        ForumPost forumPost = new ForumPost();
        forumPost.setUserId(userId);
        forumPost.setDelFlag("0"); // 只统计未删除的帖子
        
        return forumPostMapper.countForumPost(forumPost);
    }
    
    /**
     * 转换为VO对象
     * 
     * @param forumPost 帖子实体
     * @param userId 当前用户ID
     * @return 帖子VO
     */
    private ForumPostVO convertToVO(ForumPost forumPost, Long userId) {
        ForumPostVO postVO = new ForumPostVO();
        BeanUtils.copyProperties(forumPost, postVO);
        
        // 处理图片
        if (StringUtils.isNotEmpty(forumPost.getImages())) {
            try {
                List<String> imageList = objectMapper.readValue(forumPost.getImages(), new TypeReference<List<String>>() {});
                postVO.setImages(imageList);
            } catch (Exception e) {
                postVO.setImages(new ArrayList<>());
            }
        } else {
            postVO.setImages(new ArrayList<>());
        }
        
        // 匿名发帖处理
        if (Boolean.TRUE.equals(forumPost.getIsAnonymous())) {
            postVO.setUsername("匿名用户");
            postVO.setAvatar("/static/images/profile.jpg");
            postVO.setUserId(null);
        }
        
        // 设置是否点赞
        postVO.setIsLiked(false);
        if (userId != null) {
            ForumLike forumLike = new ForumLike();
            forumLike.setType(1); // 1-帖子
            forumLike.setTargetId(forumPost.getId());
            forumLike.setUserId(userId);
            ForumLike existLike = forumLikeMapper.selectForumLikeByTypeAndTargetIdAndUserId(forumLike);
            postVO.setIsLiked(existLike != null);
        }
        
        return postVO;
    }
} 