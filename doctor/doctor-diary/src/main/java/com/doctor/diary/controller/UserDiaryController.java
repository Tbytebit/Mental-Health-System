package com.doctor.diary.controller;

import com.doctor.common.core.controller.BaseController;
import com.doctor.common.core.domain.AjaxResult;
import com.doctor.common.core.page.TableDataInfo;
import com.doctor.common.utils.poi.ExcelUtil;
import com.doctor.diary.domain.UserDiary;
import com.doctor.diary.service.IUserDiaryService;
import com.doctor.diary.utils.OssHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户心理日记控制器
 */
@RestController
@RequestMapping("/diary")
public class UserDiaryController extends BaseController {
    
    @Autowired
    private IUserDiaryService userDiaryService;
    
    @Autowired
    private OssHelper ossHelper;
    
    /**
     * 获取用户日记列表
     */
    @GetMapping("/getUserDiaries")
    public TableDataInfo list(
            @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
            @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
            @RequestParam(value = "mood", required = false) String mood,
            @RequestParam(value = "keyword", required = false) String keyword) {
        
        startPage();
        List<UserDiary> list = userDiaryService.selectUserDiaryList(null, mood, keyword);
        return getDataTable(list);
    }
    
    /**
     * 获取日记详情
     */
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id) {
        return success(userDiaryService.selectUserDiaryById(id));
    }
    
    /**
     * 新增日记
     */
    @PostMapping("/addUserDiary")
    public AjaxResult add(@RequestBody UserDiary userDiary) {
        return toAjax(userDiaryService.insertUserDiary(userDiary));
    }
    
    /**
     * 新增带图片的日记
     */
    @PostMapping("/addUserDiaryWithImages")
    public AjaxResult addWithImages(
            @RequestParam(value = "diaryName", required = false) String diaryName,
            @RequestParam(value = "diaryContent", required = false) String diaryContent,
            @RequestParam(value = "mood", required = false) String mood,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        UserDiary userDiary = new UserDiary();
        userDiary.setDiaryName(diaryName);
        userDiary.setDiaryContent(diaryContent);
        userDiary.setMood(mood);
        
        return toAjax(userDiaryService.insertUserDiaryWithImages(userDiary, file));
    }
    
    /**
     * 单独上传图片
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
            
            logger.info("图片上传成功: {}", imageUrl);
            return success(data);
        } catch (Exception e) {
            logger.error("上传图片失败", e);
            return error("上传图片失败: " + e.getMessage());
        }
    }
    
    /**
     * 修改日记
     */
    @PutMapping("/{id}")
    public AjaxResult edit(@PathVariable("id") Long id, @RequestBody UserDiary userDiary) {
        userDiary.setId(id);
        return toAjax(userDiaryService.updateUserDiary(userDiary));
    }
    
    /**
     * 修改带图片的日记
     */
    @PostMapping("/updateWithImages/{id}")
    public AjaxResult editWithImages(
            @PathVariable("id") Long id,
            @RequestParam(value = "diaryName", required = false) String diaryName,
            @RequestParam(value = "diaryContent", required = false) String diaryContent,
            @RequestParam(value = "mood", required = false) String mood,
            @RequestParam(value = "keepOldImages", defaultValue = "false") Boolean keepOldImages,
            @RequestParam(value = "file", required = false) MultipartFile file) {
        
        UserDiary userDiary = new UserDiary();
        userDiary.setId(id);
        userDiary.setDiaryName(diaryName);
        userDiary.setDiaryContent(diaryContent);
        userDiary.setMood(mood);
        
        return toAjax(userDiaryService.updateUserDiaryWithImages(userDiary, file, keepOldImages));
    }
    
    /**
     * 删除日记
     */
    @DeleteMapping("/{id}")
    public AjaxResult remove(@PathVariable("id") Long id) {
        return toAjax(userDiaryService.deleteUserDiary(id));
    }
    
    /**
     * 批量删除日记
     */
    @DeleteMapping("/batch/{ids}")
    public AjaxResult batchRemove(@PathVariable Long[] ids) {
        return toAjax(userDiaryService.deleteUserDiaries(ids));
    }
} 