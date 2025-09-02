package com.doctor.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doctor.chat.entity.FriendRequest;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 好友申请Mapper接口
 */
@Mapper
public interface FriendRequestMapper extends BaseMapper<FriendRequest> {
    
    /**
     * 获取用户收到的好友申请列表
     *
     * @param userId 用户ID
     * @return 好友申请列表
     */
    List<FriendRequest> getReceivedRequests(@Param("userId") Long userId);
    
    /**
     * 获取用户发送的好友申请列表
     *
     * @param userId 用户ID
     * @return 好友申请列表
     */
    List<FriendRequest> getSentRequests(@Param("userId") Long userId);
    
    /**
     * 更新好友申请状态
     *
     * @param requestId 申请ID
     * @param status    状态
     * @return 影响行数
     */
    int updateRequestStatus(@Param("requestId") Long requestId, @Param("status") Integer status);
    
    /**
     * 统计符合条件的记录数
     *
     * @param wrapper 查询条件
     * @return 记录数
     */
    Integer selectCount(@Param("ew") Object wrapper);
} 