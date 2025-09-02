package com.doctor.web.service;

import java.util.List;

import com.doctor.web.domain.Advertisements;

/**
 * 广告Service接口
 * 
 * @author li
 * @date 2024-04-10
 */
public interface IAdvertisementsService 
{
    /**
     * 查询广告
     * 
     * @param adId 广告主键
     * @return 广告
     */
    public Advertisements selectAdvertisementsByAdId(Long adId);

    /**
     * 查询广告列表
     * 
     * @param advertisements 广告
     * @return 广告集合
     */
    public List<Advertisements> selectAdvertisementsList(Advertisements advertisements);

    /**
     * 新增广告
     * 
     * @param advertisements 广告
     * @return 结果
     */
    public int insertAdvertisements(Advertisements advertisements);

    /**
     * 修改广告
     * 
     * @param advertisements 广告
     * @return 结果
     */
    public int updateAdvertisements(Advertisements advertisements);

    /**
     * 批量删除广告
     * 
     * @param adIds 需要删除的广告主键集合
     * @return 结果
     */
    public int deleteAdvertisementsByAdIds(Long[] adIds);

    /**
     * 删除广告信息
     * 
     * @param adId 广告主键
     * @return 结果
     */
    public int deleteAdvertisementsByAdId(Long adId);
    /**
     * 获取广告轮播
     */
    List<Advertisements> getAdCarousel();
}
