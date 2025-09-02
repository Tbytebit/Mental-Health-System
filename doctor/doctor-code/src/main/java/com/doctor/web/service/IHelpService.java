package com.doctor.web.service;

import java.util.List;

import com.doctor.web.domain.Help;

/**
 * 帮助内容Service接口
 * 
 * @author LI
 * @date 2024-05-09
 */
public interface IHelpService 
{
    /**
     * 查询帮助内容
     * 
     * @param helpId 帮助内容主键
     * @return 帮助内容
     */
    public Help selectHelpByHelpId(Long helpId);

    /**
     * 查询帮助内容列表
     * 
     * @param help 帮助内容
     * @return 帮助内容集合
     */
    public List<Help> selectHelpList(Help help);

    /**
     * 新增帮助内容
     * 
     * @param help 帮助内容
     * @return 结果
     */
    public int insertHelp(Help help);

    /**
     * 修改帮助内容
     * 
     * @param help 帮助内容
     * @return 结果
     */
    public int updateHelp(Help help);

    /**
     * 批量删除帮助内容
     * 
     * @param helpIds 需要删除的帮助内容主键集合
     * @return 结果
     */
    public int deleteHelpByHelpIds(Long[] helpIds);

    /**
     * 删除帮助内容信息
     * 
     * @param helpId 帮助内容主键
     * @return 结果
     */
    public int deleteHelpByHelpId(Long helpId);
}
