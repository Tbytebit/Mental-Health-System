-- ----------------------------
-- 1. 论坛帖子表
-- ----------------------------
DROP TABLE IF EXISTS `forum_post`;
CREATE TABLE `forum_post` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `title` varchar(100) NOT NULL COMMENT '帖子标题',
  `content` text NOT NULL COMMENT '帖子内容',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `avatar` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `category_id` varchar(50) DEFAULT NULL COMMENT '分类ID',
  `category_name` varchar(50) DEFAULT NULL COMMENT '分类名称',
  `images` text DEFAULT NULL COMMENT '图片列表，JSON字符串',
  `is_anonymous` tinyint(1) DEFAULT 0 COMMENT '是否匿名发布，0否1是',
  `like_count` int(11) DEFAULT 0 COMMENT '点赞数量',
  `comment_count` int(11) DEFAULT 0 COMMENT '评论数量',
  `view_count` int(11) DEFAULT 0 COMMENT '浏览数量',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_category_id`(`category_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子表';

-- ----------------------------
-- 2. 论坛评论表
-- ----------------------------
DROP TABLE IF EXISTS `forum_comment`;
CREATE TABLE `forum_comment` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` bigint(20) NOT NULL COMMENT '帖子ID',
  `content` varchar(500) NOT NULL COMMENT '评论内容',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `username` varchar(50) DEFAULT NULL COMMENT '用户名',
  `avatar` varchar(255) DEFAULT NULL COMMENT '用户头像',
  `parent_id` bigint(20) DEFAULT 0 COMMENT '父评论ID，如果是一级评论则为0',
  `reply_id` bigint(20) DEFAULT NULL COMMENT '回复评论ID',
  `reply_user_id` bigint(20) DEFAULT NULL COMMENT '回复用户ID',
  `reply_username` varchar(50) DEFAULT NULL COMMENT '回复用户名',
  `like_count` int(11) DEFAULT 0 COMMENT '点赞数量',
  `is_anonymous` tinyint(1) DEFAULT 0 COMMENT '是否匿名评论，0否1是',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_post_id`(`post_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE,
  INDEX `idx_parent_id`(`parent_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='论坛评论表';

-- ----------------------------
-- 3. 论坛分类表
-- ----------------------------
DROP TABLE IF EXISTS `forum_category`;
CREATE TABLE `forum_category` (
  `id` varchar(50) NOT NULL COMMENT '分类ID',
  `name` varchar(50) NOT NULL COMMENT '分类名称',
  `description` varchar(255) DEFAULT NULL COMMENT '分类描述',
  `order_num` int(4) DEFAULT 0 COMMENT '显示顺序',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛分类表';

-- ----------------------------
-- 初始化论坛分类数据
-- ----------------------------
INSERT INTO `forum_category` VALUES ('anxiety', '焦虑情绪', '关于焦虑症状、管理和治疗的讨论', 1, '0', 'admin', NOW(), '', NULL, NULL);
INSERT INTO `forum_category` VALUES ('depression', '抑郁症状', '关于抑郁症状、管理和治疗的讨论', 2, '0', 'admin', NOW(), '', NULL, NULL);
INSERT INTO `forum_category` VALUES ('stress', '压力管理', '关于处理工作、学习和生活压力的讨论', 3, '0', 'admin', NOW(), '', NULL, NULL);
INSERT INTO `forum_category` VALUES ('relationship', '人际关系', '关于家庭、朋友和伴侣关系问题的讨论', 4, '0', 'admin', NOW(), '', NULL, NULL);
INSERT INTO `forum_category` VALUES ('career', '职业发展', '关于职业规划、工作压力和职场心理健康的讨论', 5, '0', 'admin', NOW(), '', NULL, NULL);
INSERT INTO `forum_category` VALUES ('family', '家庭问题', '关于亲子关系、婚姻和家庭矛盾的讨论', 6, '0', 'admin', NOW(), '', NULL, NULL);
INSERT INTO `forum_category` VALUES ('other', '其他话题', '其他与心理健康相关的讨论', 7, '0', 'admin', NOW(), '', NULL, NULL);

-- ----------------------------
-- 4. 论坛点赞表
-- ----------------------------
DROP TABLE IF EXISTS `forum_like`;
CREATE TABLE `forum_like` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `type` int(1) NOT NULL COMMENT '点赞类型：1-帖子，2-评论',
  `target_id` bigint(20) NOT NULL COMMENT '帖子/评论ID',
  `user_id` bigint(20) NOT NULL COMMENT '用户ID',
  `del_flag` char(1) DEFAULT '0' COMMENT '删除标志（0代表存在 1代表删除）',
  `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `remark` varchar(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE KEY `uk_type_target_user` (`type`,`target_id`,`user_id`,`del_flag`) USING BTREE,
  INDEX `idx_target_id`(`target_id`) USING BTREE,
  INDEX `idx_user_id`(`user_id`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=100 DEFAULT CHARSET=utf8mb4 COMMENT='论坛点赞表'; 