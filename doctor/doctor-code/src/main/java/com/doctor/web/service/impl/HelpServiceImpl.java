package com.doctor.web.service.impl;

import java.util.List;

import com.doctor.web.domain.Help;
import com.doctor.web.mapper.HelpMapper;
import com.doctor.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doctor.web.service.IHelpService;

/**
 * 帮助内容Service业务层处理
 * 
 * @author LI
 * @date 2024-05-09
 */
@Service
public class HelpServiceImpl implements IHelpService 
{
    @Autowired
    private HelpMapper helpMapper;

    /**
     * 查询帮助内容
     * 
     * @param helpId 帮助内容主键
     * @return 帮助内容
     */
    @Override
    public Help selectHelpByHelpId(Long helpId)
    {
        return helpMapper.selectHelpByHelpId(helpId);
    }

    /**
     * 查询帮助内容列表
     * 
     * @param help 帮助内容
     * @return 帮助内容
     */
    @Override
    public List<Help> selectHelpList(Help help)
    {
        return helpMapper.selectHelpList(help);
    }

    /**
     * 新增帮助内容
     * 
     * @param help 帮助内容
     * @return 结果
     */
    @Override
    public int insertHelp(Help help)
    {
        help.setCreateTime(DateUtils.getNowDate());
        return helpMapper.insertHelp(help);
    }

    /**
     * 修改帮助内容
     * 
     * @param help 帮助内容
     * @return 结果
     */
    @Override
    public int updateHelp(Help help)
    {
        help.setUpdateTime(DateUtils.getNowDate());
        return helpMapper.updateHelp(help);
    }

    /**
     * 批量删除帮助内容
     * 
     * @param helpIds 需要删除的帮助内容主键
     * @return 结果
     */
    @Override
    public int deleteHelpByHelpIds(Long[] helpIds)
    {
        return helpMapper.deleteHelpByHelpIds(helpIds);
    }

    /**
     * 删除帮助内容信息
     * 
     * @param helpId 帮助内容主键
     * @return 结果
     */
    @Override
    public int deleteHelpByHelpId(Long helpId)
    {
        return helpMapper.deleteHelpByHelpId(helpId);
    }
}
