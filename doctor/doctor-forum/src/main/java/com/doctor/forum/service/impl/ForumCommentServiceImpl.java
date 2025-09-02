package com.doctor.forum.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.doctor.common.utils.SecurityUtils;
import com.doctor.common.utils.StringUtils;
import com.doctor.system.service.ISysUserService;
import com.doctor.forum.domain.entity.ForumComment;
import com.doctor.forum.domain.entity.ForumLike;
import com.doctor.forum.domain.dto.ForumCommentDTO;
import com.doctor.forum.domain.vo.ForumCommentVO;
import com.doctor.forum.mapper.ForumCommentMapper;
import com.doctor.forum.mapper.ForumLikeMapper;
import com.doctor.forum.mapper.ForumPostMapper;
import com.doctor.forum.service.IForumCommentService;

/**
 * 论坛评论Service实现类
 */
@Service
public class ForumCommentServiceImpl implements IForumCommentService {
    
    @Autowired
    private ForumCommentMapper forumCommentMapper;
    
    @Autowired
    private ForumLikeMapper forumLikeMapper;
    
    @Autowired
    private ForumPostMapper forumPostMapper;
    
    @Autowired
    private ISysUserService userService;
    
    /**
     * 查询评论列表
     * 
     * @param postId 帖子ID
     * @param userId 当前用户ID
     * @return 评论列表
     */
    @Override
    public List<ForumCommentVO> selectForumCommentList(Long postId, Long userId) {
        // 查询帖子的所有评论
        ForumComment forumComment = new ForumComment();
        forumComment.setPostId(postId);
        List<ForumComment> commentList = forumCommentMapper.selectForumCommentList(forumComment);
        
        // 转换为VO并构建评论树
        return buildCommentTree(commentList, userId);
    }
    
    /**
     * 查询评论详情
     * 
     * @param id 评论ID
     * @param userId 当前用户ID
     * @return 评论详情
     */
    @Override
    public ForumCommentVO selectForumCommentById(Long id, Long userId) {
        ForumComment forumComment = forumCommentMapper.selectForumCommentById(id);
        if (forumComment == null) {
            return null;
        }
        
        return convertToVO(forumComment, userId);
    }
    
    /**
     * 新增评论
     * 
     * @param forumCommentDTO 评论信息
     * @return 结果
     */
    @Override
    @Transactional
    public ForumComment insertForumComment(ForumCommentDTO forumCommentDTO) {
        ForumComment forumComment = new ForumComment();
        BeanUtils.copyProperties(forumCommentDTO, forumComment);
        
        // 设置默认值
        if (forumComment.getUserId() == null) {
            try {
                forumComment.setUserId(SecurityUtils.getUserId());
                forumComment.setUsername(SecurityUtils.getUsername());
                forumComment.setAvatar(userService.selectUserById(forumComment.getUserId()).getAvatar());
            } catch (Exception e) {
                // 未登录状态，使用默认用户
                forumComment.setUserId(1L);
                forumComment.setUsername("匿名用户");
                forumComment.setAvatar("/static/images/profile.jpg");
            }
        } else {
            // 根据UserId获取用户名和头像
            if (StringUtils.isEmpty(forumComment.getUsername())) {
                try {
                    forumComment.setUsername(userService.selectUserById(forumComment.getUserId()).getNickName());
                    forumComment.setAvatar(userService.selectUserById(forumComment.getUserId()).getAvatar());
                } catch (Exception e) {
                    forumComment.setUsername("用户" + forumComment.getUserId());
                    forumComment.setAvatar("/static/images/profile.jpg");
                }
            }
        }
        
        // 如果是回复评论，设置回复用户信息
        if (forumComment.getReplyId() != null && forumComment.getReplyId() > 0) {
            ForumComment replyComment = forumCommentMapper.selectForumCommentById(forumComment.getReplyId());
            if (replyComment != null) {
                forumComment.setReplyUserId(replyComment.getUserId());
                
                // 如果回复的评论是匿名的，使用匿名用户名
                if (Boolean.TRUE.equals(replyComment.getIsAnonymous())) {
                    forumComment.setReplyUsername("匿名用户");
                } else {
                    forumComment.setReplyUsername(replyComment.getUsername());
                }
            }
        }
        
        // 如果未设置父评论ID，默认为0（一级评论）
        if (forumComment.getParentId() == null) {
            forumComment.setParentId(0L);
        }
        
        // 设置初始值
        forumComment.setLikeCount(0);
        forumComment.setDelFlag("0");
        
        // 插入评论
        forumCommentMapper.insertForumComment(forumComment);
        
        // 更新帖子的评论数
        forumPostMapper.increaseCommentCount(forumComment.getPostId());
        
        return forumComment;
    }
    
    /**
     * 修改评论
     * 
     * @param forumCommentDTO 评论信息
     * @return 结果
     */
    @Override
    public int updateForumComment(ForumCommentDTO forumCommentDTO) {
        ForumComment forumComment = new ForumComment();
        BeanUtils.copyProperties(forumCommentDTO, forumComment);
        
        return forumCommentMapper.updateForumComment(forumComment);
    }
    
    /**
     * 删除评论
     * 
     * @param id 评论ID
     * @return 结果
     */
    @Override
    public int deleteForumCommentById(Long id) {
        return forumCommentMapper.deleteForumCommentById(id);
    }
    
    /**
     * 批量删除评论
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteForumCommentByIds(Long[] ids) {
        return forumCommentMapper.deleteForumCommentByIds(ids);
    }
    
    /**
     * 点赞评论
     * 
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int likeForumComment(Long commentId, Long userId) {
        // 检查是否已点赞
        ForumLike forumLike = new ForumLike();
        forumLike.setType(2); // 2-评论
        forumLike.setTargetId(commentId);
        forumLike.setUserId(userId);
        
        ForumLike existLike = forumLikeMapper.selectForumLikeByTypeAndTargetIdAndUserId(forumLike);
        if (existLike != null) {
            // 已点赞，直接返回
            return 0;
        }
        
        // 增加点赞记录
        forumLikeMapper.insertForumLike(forumLike);
        
        // 更新评论点赞数
        return forumCommentMapper.increaseLikeCount(commentId);
    }
    
    /**
     * 取消点赞评论
     * 
     * @param commentId 评论ID
     * @param userId 用户ID
     * @return 结果
     */
    @Override
    @Transactional
    public int unlikeForumComment(Long commentId, Long userId) {
        // 检查是否已点赞
        ForumLike forumLike = new ForumLike();
        forumLike.setType(2); // 2-评论
        forumLike.setTargetId(commentId);
        forumLike.setUserId(userId);
        
        ForumLike existLike = forumLikeMapper.selectForumLikeByTypeAndTargetIdAndUserId(forumLike);
        if (existLike == null) {
            // 未点赞，直接返回
            return 0;
        }
        
        // 删除点赞记录
        forumLikeMapper.deleteForumLikeByTypeAndTargetIdAndUserId(forumLike);
        
        // 更新评论点赞数
        return forumCommentMapper.decreaseLikeCount(commentId);
    }
    
    /**
     * 构建评论树
     * 
     * @param commentList 评论列表
     * @param userId 当前用户ID
     * @return 评论树
     */
    private List<ForumCommentVO> buildCommentTree(List<ForumComment> commentList, Long userId) {
        List<ForumCommentVO> result = new ArrayList<>();
        Map<Long, ForumCommentVO> commentMap = new HashMap<>();
        
        // 先转换为VO对象
        for (ForumComment comment : commentList) {
            ForumCommentVO commentVO = convertToVO(comment, userId);
            commentMap.put(comment.getId(), commentVO);
        }
        
        // 构建评论树
        for (ForumCommentVO commentVO : commentMap.values()) {
            if (commentVO.getParentId() == 0L) {
                // 一级评论
                result.add(commentVO);
            } else {
                // 子评论
                ForumCommentVO parentComment = commentMap.get(commentVO.getParentId());
                if (parentComment != null) {
                    if (parentComment.getChildren() == null) {
                        parentComment.setChildren(new ArrayList<>());
                    }
                    parentComment.getChildren().add(commentVO);
                }
            }
        }
        
        return result;
    }
    
    /**
     * 转换为VO对象
     * 
     * @param forumComment 评论实体
     * @param userId 当前用户ID
     * @return 评论VO
     */
    private ForumCommentVO convertToVO(ForumComment forumComment, Long userId) {
        ForumCommentVO commentVO = new ForumCommentVO();
        BeanUtils.copyProperties(forumComment, commentVO);
        
        // 匿名评论处理
        if (Boolean.TRUE.equals(forumComment.getIsAnonymous())) {
            commentVO.setUsername("匿名用户");
            commentVO.setAvatar("/static/images/profile.jpg");
            commentVO.setUserId(null);
        }
        
        // 设置是否点赞
        commentVO.setIsLiked(false);
        if (userId != null) {
            ForumLike forumLike = new ForumLike();
            forumLike.setType(2); // 2-评论
            forumLike.setTargetId(forumComment.getId());
            forumLike.setUserId(userId);
            ForumLike existLike = forumLikeMapper.selectForumLikeByTypeAndTargetIdAndUserId(forumLike);
            commentVO.setIsLiked(existLike != null);
        }
        
        // 初始化子评论列表
        commentVO.setChildren(new ArrayList<>());
        
        return commentVO;
    }
} 