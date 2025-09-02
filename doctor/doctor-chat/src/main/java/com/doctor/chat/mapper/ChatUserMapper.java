package com.doctor.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doctor.chat.entity.ChatUser;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 聊天用户Mapper接口
 */
@Mapper
public interface ChatUserMapper extends BaseMapper<ChatUser> {
    
    /**
     * 更新用户在线状态
     * 
     * @param userId 用户ID
     * @param status 状态（0离线 1上线）
     * @return 影响行数
     */
    @Update("UPDATE chat_user SET status = #{status} WHERE user_id = #{userId}")
    int updateUserStatus(@Param("userId") Long userId, @Param("status") String status);
} 