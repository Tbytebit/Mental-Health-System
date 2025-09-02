package com.doctor.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doctor.chat.entity.FriendRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Date;

/**
 * 好友关系Mapper接口
 */
public interface FriendRelationMapper extends BaseMapper<FriendRelation> {
    
    /**
     * 插入好友关系
     *
     * @param relation 好友关系实体
     * @return 影响行数
     */
    int insert(FriendRelation relation);
    
    /**
     * 获取用户的好友列表
     *
     * @param userId 用户ID
     * @return 好友关系列表
     */
    List<FriendRelation> getFriendList(@Param("userId") Long userId);
    
    /**
     * 检查是否已经是好友关系
     *
     * @param userId   用户ID
     * @param friendId 好友ID
     * @return 是否是好友
     */
    int checkFriendRelation(@Param("userId") Long userId, @Param("friendId") Long friendId);
    
    /**
     * 删除好友关系（双向删除）
     *
     * @param userId   用户ID
     * @param friendId 好友ID
     * @return 影响行数
     */
    int deleteFriendRelation(@Param("userId") Long userId, @Param("friendId") Long friendId);
    
    /**
     * 根据昵称搜索用户
     *
     * @param keyword 关键词
     * @param userId 当前用户ID
     * @return 用户列表
     */
    List<Map<String, Object>> searchUsersByNickname(@Param("keyword") String keyword, @Param("userId") Long userId);
    
    /**
     * 更新好友备注
     *
     * @param userId 用户ID
     * @param friendId 好友ID
     * @param remark 备注
     * @param updateTime 更新时间
     * @return 影响行数
     */
    int updateFriendRemark(@Param("userId") Long userId, @Param("friendId") Long friendId, 
                            @Param("remark") String remark, @Param("updateTime") Date updateTime);
                            
    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    Map<String, Object> getUserInfoById(@Param("userId") Long userId);
} 