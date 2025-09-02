package com.doctor.mood.controller;

import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONArray;
import com.doctor.ai.service.IAiChatService;
import com.doctor.mood.domain.entity.MoodQuestion;
import com.doctor.mood.service.IMoodQuestionService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.doctor.common.annotation.Log;
import com.doctor.common.core.controller.BaseController;
import com.doctor.common.core.domain.AjaxResult;
import com.doctor.common.enums.BusinessType;
import com.doctor.mood.domain.entity.MoodRecord;
import com.doctor.mood.service.IMoodRecordService;
import com.doctor.common.utils.poi.ExcelUtil;
import com.doctor.common.core.page.TableDataInfo;
import com.doctor.common.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 用户情绪量表填写记录Controller
 */
@RestController
@RequestMapping("/mood/record")
public class MoodRecordController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(MoodRecordController.class);
    
    @Autowired
    private IMoodRecordService moodRecordService;
    
    @Autowired
    private IMoodQuestionService moodQuestionService;
    
    @Autowired
    private IAiChatService aiChatService;

    /**
     * 查询用户情绪量表填写记录列表
     */
    @PreAuthorize("@ss.hasPermi('mood:record:list')")
    @GetMapping("/list")
    public TableDataInfo list(MoodRecord moodRecord)
    {
        startPage();
        List<MoodRecord> list = moodRecordService.selectMoodRecordList(moodRecord);
        return getDataTable(list);
    }
    
    /**
     * 查询当前用户的量表填写记录列表
     */
    @GetMapping("/my")
    public AjaxResult myRecords()
    {
        Long userId = SecurityUtils.getUserId();
        List<MoodRecord> list = moodRecordService.selectMoodRecordByUserId(userId);
        return success(list);
    }
    
    /**
     * 查询当前用户指定量表的填写记录
     */
    @GetMapping("/my/{scaleId}")
    public AjaxResult myScaleRecords(@PathVariable("scaleId") Long scaleId)
    {
        Long userId = SecurityUtils.getUserId();
        List<MoodRecord> list = moodRecordService.selectMoodRecordByUserIdAndScaleId(userId, scaleId);
        return success(list);
    }

    /**
     * 导出用户情绪量表填写记录列表
     */
    @PreAuthorize("@ss.hasPermi('mood:record:export')")
    @Log(title = "用户情绪量表填写记录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, MoodRecord moodRecord)
    {
        List<MoodRecord> list = moodRecordService.selectMoodRecordList(moodRecord);
        ExcelUtil<MoodRecord> util = new ExcelUtil<MoodRecord>(MoodRecord.class);
        util.exportExcel(response, list, "用户情绪量表填写记录数据");
    }

    /**
     * 获取用户情绪量表填写记录详细信息
     */
    @GetMapping(value = "/{recordId}")
    public AjaxResult getInfo(@PathVariable("recordId") Long recordId)
    {
        return success(moodRecordService.selectMoodRecordByRecordId(recordId));
    }

    /**
     * 新增用户情绪量表填写记录
     */
    @Log(title = "用户情绪量表填写记录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody MoodRecord moodRecord)
    {
        try {
            log.info("收到情绪量表提交: scaleId={}, totalScore={}", moodRecord.getScaleId(), moodRecord.getTotalScore());
            
            moodRecord.setUserId(SecurityUtils.getUserId());
            moodRecord.setCompleteTime(new Date());
            moodRecord.setStatus("1"); // 已完成
            
            // 解析答题内容
            String answerContent = moodRecord.getAnswerContent();
            log.info("答题内容: {}", answerContent);
            
            if (answerContent != null && !answerContent.isEmpty()) {
                try {
                    // 获取量表的所有问题
                    List<MoodQuestion> questions = moodQuestionService.selectMoodQuestionByScaleId(moodRecord.getScaleId());
                    
                    List<String> questionTexts = questions.stream()
                            .sorted((q1, q2) -> q1.getOrderNum().compareTo(q2.getOrderNum()))
                            .map(MoodQuestion::getQuestionContent)
                            .collect(Collectors.toList());
                    
                    List<String> answerTexts = new ArrayList<>();
                    
                    // 尝试解析不同格式的JSON
                    if (answerContent.startsWith("[")) {
                        // 新格式: [{"questionId":1,"answer":4,"text":"选项文本"}]
                        try {
                            JSONArray answers = JSON.parseArray(answerContent);
                            for (int i = 0; i < answers.size(); i++) {
                                JSONObject answer = answers.getJSONObject(i);
                                answerTexts.add(answer.getString("text"));
                            }
                            log.info("使用数组格式解析答题内容成功，共{}个答案", answerTexts.size());
                        } catch (Exception e) {
                            log.error("解析数组格式答案内容失败", e);
                            throw e;
                        }
                    } else if (answerContent.startsWith("{")) {
                        // 旧格式: {"1":"4","2":"4","3":"4","4":"4","5":"4"}
                        try {
                            JSONObject answers = JSON.parseObject(answerContent);
                            // 获取所有问题对应的选项
                            Map<String, JSONArray> questionOptionsMap = new HashMap<>();
                            for (MoodQuestion question : questions) {
                                String optionJson = question.getQuestionOptions();
                                if (optionJson != null && !optionJson.isEmpty()) {
                                    questionOptionsMap.put(
                                        question.getQuestionId().toString(),
                                        JSON.parseArray(optionJson)
                                    );
                                }
                            }
                            
                            // 创建一个映射，存储选项值到选项文本的对应关系
                            Map<String, String> optionValueTextMap = new HashMap<>();
                            optionValueTextMap.put("1", "很少");
                            optionValueTextMap.put("2", "有时");
                            optionValueTextMap.put("3", "经常");
                            optionValueTextMap.put("4", "持续");
                            
                            // 按照问题ID排序处理答案
                            for (MoodQuestion question : questions) {
                                String questionId = question.getQuestionId().toString();
                                String selectedValue = answers.getString(questionId);
                                
                                if (selectedValue != null) {
                                    // 找到对应选项的文本
                                    String optionText = optionValueTextMap.getOrDefault(selectedValue, "未知选项");
                                    
                                    // 记录问题和选择的选项
                                    log.debug("问题 {} 选择了选项值 {}, 文本: {}", questionId, selectedValue, optionText);
                                    
                                    answerTexts.add(optionText);
                                } else {
                                    answerTexts.add("未作答");
                                }
                            }
                            log.info("使用对象格式解析答题内容成功，共{}个答案", answerTexts.size());
                        } catch (Exception e) {
                            log.error("解析对象格式答案内容失败", e);
                            throw e;
                        }
                    } else {
                        throw new IllegalArgumentException("无法识别的答题内容格式");
                    }
                    
                    // 如果成功解析到答案
                    if (!answerTexts.isEmpty()) {
                        // 确保问题和答案数量一致
                        while (answerTexts.size() < questionTexts.size()) {
                            answerTexts.add("未作答");
                        }
                        while (questionTexts.size() < answerTexts.size()) {
                            questionTexts.add("未知问题");
                        }
                        
                        // 构建问题和答案的组合列表，用于记录
                        List<String> questionAnswerPairs = new ArrayList<>();
                        for (int i = 0; i < questionTexts.size(); i++) {
                            String qa = String.format("%s - %s", questionTexts.get(i), answerTexts.get(i));
                            questionAnswerPairs.add(qa);
                        }
                        
                        log.info("问题列表: {}", questionTexts);
                        log.info("答案列表: {}", answerTexts);
                        log.info("问题-答案对应: {}", questionAnswerPairs);
                        
                        try {
                            String aiAnalysis = aiChatService.analyzeMoodScaleResult(
                                    questionTexts, 
                                    answerTexts, 
                                    moodRecord.getScaleId(), 
                                    moodRecord.getTotalScore());
                            
                            // 将AI分析结果设置为评估结果
                            moodRecord.setEvaluationResult(aiAnalysis);
                            log.info("情绪量表分析完成，结果长度: {}", aiAnalysis.length());
                        } catch (Exception e) {
                            log.error("调用AI服务分析情绪量表时发生错误", e);
                            moodRecord.setEvaluationResult("调用AI分析服务时出错，无法提供评估结果。请联系管理员。");
                        }
                    } else {
                        moodRecord.setEvaluationResult("无法解析答题内容，请联系管理员。");
                        log.warn("成功解析JSON但未找到有效答案");
                    }
                } catch (Exception e) {
                    log.error("解析答案内容失败", e);
                    moodRecord.setEvaluationResult("解析答案出错，无法进行评估。错误: " + e.getMessage());
                }
            } else {
                moodRecord.setEvaluationResult("答题内容为空，无法进行评估。");
                log.warn("答题内容为空");
            }
            
            int result = moodRecordService.insertMoodRecord(moodRecord);
            return success(moodRecord.getRecordId());
        } catch (Exception e) {
            log.error("处理情绪量表记录时发生错误", e);
            return AjaxResult.error("保存记录失败: " + e.getMessage());
        }
    }

    /**
     * 修改用户情绪量表填写记录
     */
    @PreAuthorize("@ss.hasPermi('mood:record:edit')")
    @Log(title = "用户情绪量表填写记录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody MoodRecord moodRecord)
    {
        return toAjax(moodRecordService.updateMoodRecord(moodRecord));
    }

    /**
     * 删除用户情绪量表填写记录
     */
    @PreAuthorize("@ss.hasPermi('mood:record:remove')")
    @Log(title = "用户情绪量表填写记录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{recordIds}")
    public AjaxResult remove(@PathVariable Long[] recordIds)
    {
        return toAjax(moodRecordService.deleteMoodRecordByRecordIds(recordIds));
    }
} 