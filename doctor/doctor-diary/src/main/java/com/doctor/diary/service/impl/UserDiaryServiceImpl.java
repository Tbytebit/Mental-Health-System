package com.doctor.diary.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.doctor.diary.domain.UserDiary;
import com.doctor.diary.mapper.UserDiaryMapper;
import com.doctor.diary.service.IUserDiaryService;
import com.doctor.diary.utils.OssHelper;
import com.doctor.common.utils.SecurityUtils;
import com.doctor.common.utils.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * 用户心理日记服务实现
 */
@Service
public class UserDiaryServiceImpl implements IUserDiaryService {

    private static final Logger logger = LoggerFactory.getLogger(UserDiaryServiceImpl.class);

    @Autowired
    private UserDiaryMapper userDiaryMapper;
    
    @Autowired
    private OssHelper ossHelper;

    /**
     * 查询日记详情
     *
     * @param id 日记ID
     * @return 日记信息
     */
    @Override
    public UserDiary selectUserDiaryById(Long id) {
        return userDiaryMapper.selectUserDiaryById(id);
    }

    /**
     * 查询用户的日记列表
     *
     * @param userId 用户ID
     * @param mood 心情过滤
     * @param keyword 关键词搜索
     * @return 日记列表
     */
    @Override
    public List<UserDiary> selectUserDiaryList(Long userId, String mood, String keyword) {
        if (userId == null) {
            userId = SecurityUtils.getUserId();
        }
        return userDiaryMapper.selectUserDiaryList(userId, mood, keyword);
    }

    /**
     * 新增日记
     *
     * @param userDiary 日记信息
     * @return 结果
     */
    @Override
    public int insertUserDiary(UserDiary userDiary) {
        userDiary.setUserId(SecurityUtils.getUserId());
        userDiary.setCreateTime(new Date());
        userDiary.setUpdateTime(new Date());
        userDiary.setDelFlag("0");
        return userDiaryMapper.insertUserDiary(userDiary);
    }

    /**
     * 新增带图片的日记
     *
     * @param userDiary 日记信息
     * @param file 图片文件
     * @return 结果
     */
    @Override
    public int insertUserDiaryWithImages(UserDiary userDiary, MultipartFile file) {
        if (file != null && !file.isEmpty()) {
            try {
                // 上传图片到OSS
                String imageUrl = ossHelper.uploadFile(file);
                
                // 处理图片JSON
                List<String> imagesList = new ArrayList<>();
                imagesList.add(imageUrl);
                userDiary.setImages(new JSONArray(imagesList).toString());
                
                logger.info("日记图片上传成功，OSS地址: {}", imageUrl);
            } catch (Exception e) {
                logger.error("上传图片到OSS失败", e);
            }
        }
        return insertUserDiary(userDiary);
    }

    /**
     * 修改日记
     *
     * @param userDiary 日记信息
     * @return 结果
     */
    @Override
    public int updateUserDiary(UserDiary userDiary) {
        userDiary.setUpdateTime(new Date());
        return userDiaryMapper.updateUserDiary(userDiary);
    }

    /**
     * 修改带图片的日记
     *
     * @param userDiary 日记信息
     * @param file 图片文件
     * @param keepOldImages 是否保留原有图片
     * @return 结果
     */
    @Override
    public int updateUserDiaryWithImages(UserDiary userDiary, MultipartFile file, boolean keepOldImages) {
        // 如果需要保留原有图片并且当前有新图片
        if (keepOldImages && file != null && !file.isEmpty()) {
            try {
                // 获取原有图片列表
                UserDiary oldDiary = userDiaryMapper.selectUserDiaryById(userDiary.getId());
                List<String> oldImagesList = new ArrayList<>();
                
                if (oldDiary != null && StringUtils.isNotEmpty(oldDiary.getImages())) {
                    try {
                        JSONArray oldImagesArray = new JSONArray(oldDiary.getImages());
                        for (int i = 0; i < oldImagesArray.length(); i++) {
                            oldImagesList.add(oldImagesArray.getString(i));
                        }
                    } catch (JSONException e) {
                        // 如果解析失败，可能是单个图片URL
                        if (StringUtils.isNotEmpty(oldDiary.getImages())) {
                            oldImagesList.add(oldDiary.getImages());
                        }
                    }
                }
                
                // 上传新图片到OSS
                String newImageUrl = ossHelper.uploadFile(file);
                oldImagesList.add(newImageUrl);
                
                // 更新图片列表
                userDiary.setImages(new JSONArray(oldImagesList).toString());
                logger.info("更新日记，添加新OSS图片: {}", newImageUrl);
                
            } catch (Exception e) {
                logger.error("更新日记时上传图片到OSS失败", e);
            }
        } 
        // 如果不保留原有图片，只使用新图片
        else if (file != null && !file.isEmpty()) {
            try {
                // 获取原有图片信息，以便删除旧图片
                UserDiary oldDiary = userDiaryMapper.selectUserDiaryById(userDiary.getId());
                if (oldDiary != null && StringUtils.isNotEmpty(oldDiary.getImages())) {
                    try {
                        JSONArray oldImagesArray = new JSONArray(oldDiary.getImages());
                        for (int i = 0; i < oldImagesArray.length(); i++) {
                            String oldImageUrl = oldImagesArray.getString(i);
                            // 删除旧图片（可选操作）
                            if (oldImageUrl.contains(ossHelper.getBucketName())) {
                                try {
                                    ossHelper.deleteFile(oldImageUrl);
                                    logger.info("删除旧OSS图片: {}", oldImageUrl);
                                } catch (Exception e) {
                                    logger.error("删除旧OSS图片失败: {}", oldImageUrl, e);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        logger.error("解析旧图片JSON失败", e);
                    }
                }

                // 上传新图片到OSS
                String imageUrl = ossHelper.uploadFile(file);
                List<String> imagesList = new ArrayList<>();
                imagesList.add(imageUrl);
                userDiary.setImages(new JSONArray(imagesList).toString());
                logger.info("更新日记，使用新OSS图片: {}", imageUrl);
            } catch (Exception e) {
                logger.error("更新日记图片失败", e);
            }
        } else if (!keepOldImages) {
            // 如果不保留原有图片，且没有新图片，清空图片字段
            userDiary.setImages("[]");
        }
        
        return updateUserDiary(userDiary);
    }

    /**
     * 删除日记
     *
     * @param id 日记ID
     * @return 结果
     */
    @Override
    public int deleteUserDiary(Long id) {
        return userDiaryMapper.deleteUserDiaryById(id);
    }

    /**
     * 批量删除日记
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUserDiaries(Long[] ids) {
        return userDiaryMapper.deleteUserDiaryByIds(ids);
    }
} 