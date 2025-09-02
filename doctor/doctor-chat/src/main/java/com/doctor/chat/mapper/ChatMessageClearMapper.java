package com.doctor.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.doctor.chat.entity.ChatMessageClear;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.Date;

/**
 * 聊天记录清空记录Mapper接口
 */
public interface ChatMessageClearMapper extends BaseMapper<ChatMessageClear> {
    
    /**
     * 获取用户最近一次清空聊天记录的时间
     *
     * @param userId 用户ID
     * @param friendId 好友ID
     * @return 清空时间
     */
    @Select("SELECT clear_time FROM chat_message_clear WHERE user_id = #{userId} AND friend_id = #{friendId} ORDER BY clear_time DESC LIMIT 1")
    Date getLastClearTime(@Param("userId") Long userId, @Param("friendId") Long friendId);
    
    /**
     * 删除用户与好友之间的旧清空记录
     *
     * @param userId 用户ID
     * @param friendId 好友ID
     * @return 影响行数
     */
    @Delete("DELETE FROM chat_message_clear WHERE user_id = #{userId} AND friend_id = #{friendId}")
    int deleteOldClearRecords(@Param("userId") Long userId, @Param("friendId") Long friendId);
} 