package com.doctor.forum.controller;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import com.doctor.common.core.controller.BaseController;
import com.doctor.common.core.domain.AjaxResult;
import com.doctor.common.core.page.TableDataInfo;
import com.doctor.common.utils.SecurityUtils;
import com.doctor.common.utils.StringUtils;
import com.doctor.forum.domain.entity.ForumPost;
import com.doctor.forum.domain.entity.ForumComment;
import com.doctor.forum.domain.entity.ForumCategory;
import com.doctor.forum.domain.dto.ForumPostDTO;
import com.doctor.forum.domain.dto.ForumCommentDTO;
import com.doctor.forum.domain.vo.ForumPostVO;
import com.doctor.forum.domain.vo.ForumCommentVO;
import com.doctor.forum.service.IForumPostService;
import com.doctor.forum.service.IForumCommentService;
import com.doctor.forum.service.IForumCategoryService;
import com.doctor.forum.utils.OssHelper;
import org.springframework.beans.factory.annotation.Qualifier;
import java.util.ArrayList;

/**
 * 论坛控制器
 */
@RestController
@RequestMapping("/api/forum")
public class ForumController extends BaseController {
    
    @Autowired
    private IForumPostService forumPostService;
    
    @Autowired
    private IForumCommentService forumCommentService;
    
    @Autowired
    private IForumCategoryService forumCategoryService;
    
    @Autowired
    @Qualifier("forumOssHelper")
    private OssHelper ossHelper;
    
    /**
     * 获取帖子列表
     */
    @GetMapping("/list")
    public TableDataInfo list(ForumPost forumPost) {
        startPage();
        // 设置查询条件
        if (StringUtils.isNotEmpty(forumPost.getTitle())) {
            forumPost.setTitle(forumPost.getTitle().trim());
        }
        if (StringUtils.isNotEmpty(forumPost.getContent())) {
            forumPost.setContent(forumPost.getContent().trim());
        }
        
        List<ForumPostVO> list = forumPostService.selectForumPostList(forumPost);
        return getDataTable(list);
    }
    
    /**
     * 获取帖子详情
     */
    @GetMapping("/detail/{id}")
    public AjaxResult getDetail(@PathVariable("id") Long id) {
        Long userId = null;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            // 未登录状态，忽略
        }
        
        ForumPostVO forumPost = forumPostService.selectForumPostById(id, userId);
        return AjaxResult.success(forumPost);
    }
    
    /**
     * 获取帖子（调试用，忽略删除标记）
     */
    @GetMapping("/get/{id}")
    public AjaxResult get(@PathVariable("id") Long id) {
        ForumPostVO forumPost = forumPostService.selectForumPostById(id, null);
        return AjaxResult.success(forumPost);
    }
    
    /**
     * 创建帖子
     */
    @PostMapping("/create")
    public AjaxResult create(@RequestBody ForumPostDTO forumPostDTO) {
        if (StringUtils.isEmpty(forumPostDTO.getTitle()) || StringUtils.isEmpty(forumPostDTO.getContent())) {
            return AjaxResult.error("标题和内容不能为空");
        }
        
        // 设置当前用户
        if (forumPostDTO.getUserId() == null) {
            try {
                forumPostDTO.setUserId(SecurityUtils.getUserId());
            } catch (Exception e) {
                // 未登录状态，使用默认用户ID
                forumPostDTO.setUserId(1L);
            }
        }
        
        // 创建帖子
        ForumPost post = forumPostService.insertForumPost(forumPostDTO);
        
        return AjaxResult.success(post);
    }
    
    /**
     * 点赞帖子
     */
    @PostMapping("/like/{postId}")
    public AjaxResult like(@PathVariable("postId") Long postId) {
        Long userId;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            return AjaxResult.error("请先登录");
        }
        
        forumPostService.likeForumPost(postId, userId);
        return AjaxResult.success();
    }
    
    /**
     * 取消点赞帖子
     */
    @PostMapping("/unlike/{postId}")
    public AjaxResult unlike(@PathVariable("postId") Long postId) {
        Long userId;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            return AjaxResult.error("请先登录");
        }
        
        forumPostService.unlikeForumPost(postId, userId);
        return AjaxResult.success();
    }
    
    /**
     * 获取评论列表
     */
    @GetMapping("/comments/{postId}")
    public AjaxResult getComments(@PathVariable("postId") Long postId) {
        Long userId = null;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            // 未登录状态，忽略
        }
        
        List<ForumCommentVO> commentList = forumCommentService.selectForumCommentList(postId, userId);
        return AjaxResult.success(commentList);
    }
    
    /**
     * 发表评论
     */
    @PostMapping("/comment")
    public AjaxResult comment(@RequestBody ForumCommentDTO forumCommentDTO) {
        if (StringUtils.isEmpty(forumCommentDTO.getContent())) {
            return AjaxResult.error("评论内容不能为空");
        }
        
        if (forumCommentDTO.getPostId() == null) {
            return AjaxResult.error("帖子ID不能为空");
        }
        
        // 设置当前用户
        if (forumCommentDTO.getUserId() == null) {
            try {
                forumCommentDTO.setUserId(SecurityUtils.getUserId());
            } catch (Exception e) {
                // 未登录状态，使用默认用户ID
                forumCommentDTO.setUserId(1L);
            }
        }
        
        // 创建评论
        ForumComment comment = forumCommentService.insertForumComment(forumCommentDTO);
        
        return AjaxResult.success(comment);
    }
    
    /**
     * 回复评论
     */
    @PostMapping("/comment/reply")
    public AjaxResult reply(@RequestBody ForumCommentDTO forumCommentDTO) {
        if (StringUtils.isEmpty(forumCommentDTO.getContent())) {
            return AjaxResult.error("评论内容不能为空");
        }
        
        if (forumCommentDTO.getPostId() == null) {
            return AjaxResult.error("帖子ID不能为空");
        }
        
        if (forumCommentDTO.getParentId() == null || forumCommentDTO.getReplyId() == null) {
            return AjaxResult.error("父评论ID和回复评论ID不能为空");
        }
        
        // 设置当前用户
        if (forumCommentDTO.getUserId() == null) {
            try {
                forumCommentDTO.setUserId(SecurityUtils.getUserId());
            } catch (Exception e) {
                // 未登录状态，使用默认用户ID
                forumCommentDTO.setUserId(1L);
            }
        }
        
        // 创建评论
        ForumComment comment = forumCommentService.insertForumComment(forumCommentDTO);
        
        return AjaxResult.success(comment);
    }
    
    /**
     * 点赞评论
     */
    @PostMapping("/comment/like/{commentId}")
    public AjaxResult likeComment(@PathVariable("commentId") Long commentId) {
        Long userId;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            return AjaxResult.error("请先登录");
        }
        
        forumCommentService.likeForumComment(commentId, userId);
        return AjaxResult.success();
    }
    
    /**
     * 取消点赞评论
     */
    @PostMapping("/comment/unlike/{commentId}")
    public AjaxResult unlikeComment(@PathVariable("commentId") Long commentId) {
        Long userId;
        try {
            userId = SecurityUtils.getUserId();
        } catch (Exception e) {
            return AjaxResult.error("请先登录");
        }
        
        forumCommentService.unlikeForumComment(commentId, userId);
        return AjaxResult.success();
    }
    
    /**
     * 获取分类列表
     */
    @GetMapping("/categories")
    public AjaxResult getCategories() {
        List<ForumCategory> categoryList = forumCategoryService.selectForumCategoryAll();
        return AjaxResult.success(categoryList);
    }
    
    /**
     * 上传图片
     */
    @PostMapping("/uploadImage")
    public AjaxResult uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return error("上传图片不能为空");
            }
            
            // 上传到OSS
            String imageUrl = ossHelper.uploadFile(file);
            
            // 构建返回数据
            Map<String, Object> data = new HashMap<>();
            data.put("url", imageUrl);
            data.put("fileName", imageUrl.substring(imageUrl.lastIndexOf("/") + 1));
            data.put("originalName", file.getOriginalFilename());
            data.put("size", file.getSize());
            data.put("type", file.getContentType());
            data.put("oss", true);
            
            logger.info("论坛图片上传成功: {}", imageUrl);
            return success(data);
        } catch (Exception e) {
            logger.error("上传图片失败", e);
            return error("上传图片失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户资料
     */
    @GetMapping("/user/{userId}")
    public AjaxResult getUserProfile(@PathVariable("userId") Long userId) {
        if (userId == null) {
            return AjaxResult.error("用户ID不能为空");
        }
        
        try {
            // 获取用户基本信息
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("id", userId);
            
            // 这里应该调用用户服务获取用户信息，暂时使用mock数据
            userInfo.put("username", "用户" + userId);
            userInfo.put("avatar", "/static/images/profile.jpg");
            userInfo.put("introduction", "这是用户" + userId + "的个人介绍");
            
            // 获取用户发帖数量
            int postCount = forumPostService.countUserPosts(userId);
            userInfo.put("postCount", postCount);
            
            // 模拟粉丝数和关注数
            userInfo.put("fansCount", Math.max(0, (int)(Math.random() * 100)));
            userInfo.put("followCount", Math.max(0, (int)(Math.random() * 50)));
            
            return AjaxResult.success(userInfo);
        } catch (Exception e) {
            logger.error("获取用户资料失败", e);
            return AjaxResult.error("获取用户资料失败");
        }
    }
    
    /**
     * 获取用户发布的帖子列表
     */
    @GetMapping("/user/{userId}/posts")
    public TableDataInfo getUserPosts(@PathVariable("userId") Long userId) {
        if (userId == null) {
            return getDataTable(new ArrayList<>());
        }
        
        startPage();
        ForumPost forumPost = new ForumPost();
        forumPost.setUserId(userId);
        List<ForumPostVO> list = forumPostService.selectForumPostList(forumPost);
        return getDataTable(list);
    }
} 