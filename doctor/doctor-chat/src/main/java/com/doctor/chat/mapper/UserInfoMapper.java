package com.doctor.chat.mapper;

import org.apache.ibatis.annotations.Param;
import java.util.Map;

/**
 * 用户信息Mapper接口
 */
public interface UserInfoMapper {
    
    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户信息
     */
    Map<String, Object> getUserInfoById(@Param("userId") Long userId);
} 