package com.doctor.diary.service;

import com.doctor.diary.domain.UserDiary;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户心理日记服务接口
 */
public interface IUserDiaryService {
    
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
    List<UserDiary> selectUserDiaryList(Long userId, String mood, String keyword);
    
    /**
     * 新增日记
     * 
     * @param userDiary 日记信息
     * @return 结果
     */
    int insertUserDiary(UserDiary userDiary);
    
    /**
     * 新增带图片的日记
     * 
     * @param userDiary 日记信息
     * @param file 图片文件
     * @return 结果
     */
    int insertUserDiaryWithImages(UserDiary userDiary, MultipartFile file);
    
    /**
     * 修改日记
     * 
     * @param userDiary 日记信息
     * @return 结果
     */
    int updateUserDiary(UserDiary userDiary);
    
    /**
     * 修改带图片的日记
     * 
     * @param userDiary 日记信息
     * @param file 图片文件
     * @param keepOldImages 是否保留原有图片
     * @return 结果
     */
    int updateUserDiaryWithImages(UserDiary userDiary, MultipartFile file, boolean keepOldImages);
    
    /**
     * 删除日记
     * 
     * @param id 日记ID
     * @return 结果
     */
    int deleteUserDiary(Long id);
    
    /**
     * 批量删除日记
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteUserDiaries(Long[] ids);
} 