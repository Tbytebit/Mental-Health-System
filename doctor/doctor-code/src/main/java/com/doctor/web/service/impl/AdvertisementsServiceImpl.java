package com.doctor.web.service.impl;

import java.util.List;

import com.doctor.web.domain.Advertisements;
import com.doctor.web.mapper.AdvertisementsMapper;
import com.doctor.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.doctor.web.service.IAdvertisementsService;

/**
 * 广告Service业务层处理
 * 
 * @author li
 * @date 2024-04-10
 */
@Service
public class AdvertisementsServiceImpl implements IAdvertisementsService 
{
    @Autowired
    private AdvertisementsMapper advertisementsMapper;

    /**
     * 查询广告
     * 
     * @param adId 广告主键
     * @return 广告
     */
    @Override
    public Advertisements selectAdvertisementsByAdId(Long adId)
    {
        return advertisementsMapper.selectAdvertisementsByAdId(adId);
    }

    /**
     * 查询广告列表
     * 
     * @param advertisements 广告
     * @return 广告
     */
    @Override
    public List<Advertisements> selectAdvertisementsList(Advertisements advertisements)
    {
        return advertisementsMapper.selectAdvertisementsList(advertisements);
    }

    /**
     * 新增广告
     * 
     * @param advertisements 广告
     * @return 结果
     */
    @Override
    public int insertAdvertisements(Advertisements advertisements)
    {
        advertisements.setCreateTime(DateUtils.getNowDate());
        return advertisementsMapper.insertAdvertisements(advertisements);
    }

    /**
     * 修改广告
     * 
     * @param advertisements 广告
     * @return 结果
     */
    @Override
    public int updateAdvertisements(Advertisements advertisements)
    {
        advertisements.setUpdateTime(DateUtils.getNowDate());
        return advertisementsMapper.updateAdvertisements(advertisements);
    }

    /**
     * 批量删除广告
     * 
     * @param adIds 需要删除的广告主键
     * @return 结果
     */
    @Override
    public int deleteAdvertisementsByAdIds(Long[] adIds)
    {
        return advertisementsMapper.deleteAdvertisementsByAdIds(adIds);
    }

    /**
     * 删除广告信息
     * 
     * @param adId 广告主键
     * @return 结果
     */
    @Override
    public int deleteAdvertisementsByAdId(Long adId)
    {
        return advertisementsMapper.deleteAdvertisementsByAdId(adId);
    }
    /**
     * 获取广告轮播
     */
    @Override
    public List<Advertisements> getAdCarousel() {
        return advertisementsMapper.getAdCarousel();
    }
}
