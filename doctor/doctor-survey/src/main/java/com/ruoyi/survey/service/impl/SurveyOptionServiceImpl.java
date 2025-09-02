package com.doctor.survey.service.impl;

import java.util.ArrayList;
import java.util.List;
import com.doctor.common.utils.DateUtils;
import com.doctor.survey.domain.SurveyQuestionOption;
import com.doctor.survey.mapper.SurveyQuestionOptionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doctor.survey.mapper.SurveyOptionMapper;
import com.doctor.survey.domain.SurveyOption;
import com.doctor.survey.service.ISurveyOptionService;

/**
 * 调查问卷问题选项存储Service业务层处理
 * 
 * @author guo
 * @date 2024-01-19
 */
@Service
public class SurveyOptionServiceImpl implements ISurveyOptionService 
{
    @Autowired
    private SurveyOptionMapper surveyOptionMapper;

    @Autowired
    private SurveyQuestionOptionMapper surveyQuestionOptionMapper;

    /**
     * 查询调查问卷问题选项存储
     * 
     * @param optionId 调查问卷问题选项存储主键
     * @return 调查问卷问题选项存储
     */
    @Override
    public SurveyOption selectSurveyOptionByOptionId(String optionId)
    {
        return surveyOptionMapper.selectSurveyOptionByOptionId(optionId);
    }

    /**
     * 查询调查问卷问题选项存储列表
     * 
     * @param surveyOption 调查问卷问题选项存储
     * @return 调查问卷问题选项存储
     */
    @Override
    public List<SurveyOption> selectSurveyOptionList(SurveyOption surveyOption)
    {
        return surveyOptionMapper.selectSurveyOptionList(surveyOption);
    }

    /**
     * 新增调查问卷问题选项存储
     * 
     * @param surveyOption 调查问卷问题选项存储
     * @return 结果
     */
    @Override
    public int insertSurveyOption(SurveyOption surveyOption)
    {
        surveyOption.setCreateTime(DateUtils.getNowDate());
        surveyOptionMapper.insertSurveyOption(surveyOption);
        SurveyQuestionOption surveyQuestionOption = new SurveyQuestionOption();
        surveyQuestionOption.setQuestion_id(new Long(surveyOption.getQuestionId()));
        surveyQuestionOption.setOption_id(new Long(surveyOption.getOptionId()));
        return surveyQuestionOptionMapper.insert(surveyQuestionOption);
    }

    /**
     * 修改调查问卷问题选项存储
     * 
     * @param surveyOption 调查问卷问题选项存储
     * @return 结果
     */
    @Override
    public int updateSurveyOption(SurveyOption surveyOption)
    {
        surveyOption.setUpdateTime(DateUtils.getNowDate());
        return surveyOptionMapper.updateSurveyOption(surveyOption);
    }

    /**
     * 批量删除调查问卷问题选项存储
     * 
     * @param optionIds 需要删除的调查问卷问题选项存储主键
     * @return 结果
     */
    @Override
    public int deleteSurveyOptionByOptionIds(String[] optionIds)
    {
        for (String optionId : optionIds)
            surveyQuestionOptionMapper.deleteByOptionId(Long.valueOf(optionId));
        return surveyOptionMapper.deleteSurveyOptionByOptionIds(optionIds);
    }

    /**
     * 删除调查问卷问题选项存储信息
     * 
     * @param optionId 调查问卷问题选项存储主键
     * @return 结果
     */
    @Override
    public int deleteSurveyOptionByOptionId(String optionId)
    {
        surveyQuestionOptionMapper.deleteByPrimaryKey(Long.valueOf(optionId));
        return surveyOptionMapper.deleteSurveyOptionByOptionId(optionId);
    }

    @Override
    public List<SurveyOption> selectSurveyOptionListByQuestionId(String questionId) {
        List<SurveyQuestionOption> list = surveyQuestionOptionMapper.selectListById(new Long(questionId));
        List<SurveyOption> Options = new ArrayList<>(list.size());
        for (SurveyQuestionOption surveyQuestionOption : list) {
            SurveyOption up = surveyOptionMapper.selectSurveyOptionByOptionId(String.valueOf(surveyQuestionOption.getOption_id()));
            Options.add(up);
        }
        return Options;
    }
}
