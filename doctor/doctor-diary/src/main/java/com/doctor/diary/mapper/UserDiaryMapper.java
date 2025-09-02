package com.doctor.diary.mapper;

import com.doctor.diary.domain.UserDiary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 用户心理日记数据库访问层
 */
@Mapper
public interface UserDiaryMapper {
    
    /**
     * 查询日记详情
     * 
     * @param id 日记ID
     * @return 日记信息
     */
    UserDiary selectUserDiaryById(Long id);
    
    /**
     * 查询用户的日记列表
     * 
     * @param userId 用户ID
     * @param mood 心情过滤
     * @param keyword 关键词搜索
     * @return 日记列表
     */
    List<UserDiary> selectUserDiaryList(@Param("userId") Long userId, 
                                        @Param("mood") String mood, 
                                        @Param("keyword") String keyword);
    
    /**
     * 新增日记
     * 
     * @param userDiary 日记信息
     * @return 结果
     */
    int insertUserDiary(UserDiary userDiary);
    
    /**
     * 修改日记
     * 
     * @param userDiary 日记信息
     * @return 结果
     */
    int updateUserDiary(UserDiary userDiary);
    
    /**
     * 删除日记
     * 
     * @param id 日记ID
     * @return 结果
     */
    int deleteUserDiaryById(Long id);
    
    /**
     * 批量删除日记
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteUserDiaryByIds(Long[] ids);
} 