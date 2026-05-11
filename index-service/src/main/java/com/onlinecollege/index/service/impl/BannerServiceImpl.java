package com.onlinecollege.index.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.onlinecollege.common.exception.BusinessException;
import com.onlinecollege.index.cache.HotDataCache;
import com.onlinecollege.index.config.IndexCacheProperties;
import com.onlinecollege.index.entity.EduBanner;
import com.onlinecollege.index.mapper.EduBannerMapper;
import com.onlinecollege.index.service.BannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    static final String CACHE_KEY_ACTIVE_BANNERS = "banners:active:v1";
    static final String CACHE_KEY_HOME_DATA      = "home:v1";

    private final EduBannerMapper bannerMapper;
    private final HotDataCache cache;
    private final IndexCacheProperties cacheProps;

    @Override
    public EduBanner add(EduBanner b) {
        if (b.getSort() == null) b.setSort(0);
        if (b.getEnabled() == null) b.setEnabled(1);
        b.setId(null);
        bannerMapper.insert(b);
        invalidate();
        return b;
    }

    @Override
    public boolean update(Long id, EduBanner b) {
        EduBanner existed = bannerMapper.selectById(id);
        if (existed == null) {
            throw BusinessException.notFound("banner 不存在: " + id);
        }
        b.setId(id);
        // createTime 不可被覆盖；updateTime 由 MetaObjectHandler 填
        b.setCreateTime(null);
        boolean ok = bannerMapper.updateById(b) > 0;
        invalidate();
        return ok;
    }

    @Override
    public boolean delete(Long id) {
        if (bannerMapper.selectById(id) == null) {
            throw BusinessException.notFound("banner 不存在: " + id);
        }
        boolean ok = bannerMapper.deleteById(id) > 0;
        invalidate();
        return ok;
    }

    @Override
    public EduBanner getById(Long id) {
        EduBanner b = bannerMapper.selectById(id);
        if (b == null) {
            throw BusinessException.notFound("banner 不存在: " + id);
        }
        return b;
    }

    @Override
    public List<EduBanner> listAll() {
        return bannerMapper.selectList(new LambdaQueryWrapper<EduBanner>()
                .orderByAsc(EduBanner::getSort).orderByDesc(EduBanner::getId));
    }

    @Override
    public List<EduBanner> listActiveWithCache() {
        Duration ttl = Duration.ofSeconds(cacheProps.getBannerTtlSeconds());
        return cache.getOrLoad(CACHE_KEY_ACTIVE_BANNERS, ttl, this::loadActiveFromDb);
    }

    private List<EduBanner> loadActiveFromDb() {
        LocalDateTime now = LocalDateTime.now();
        List<EduBanner> all = bannerMapper.selectList(new LambdaQueryWrapper<EduBanner>()
                .eq(EduBanner::getEnabled, 1)
                .orderByAsc(EduBanner::getSort).orderByDesc(EduBanner::getId));
        List<EduBanner> active = new ArrayList<>();
        for (EduBanner b : all) {
            if (b.getStartTime() != null && b.getStartTime().isAfter(now)) continue;
            if (b.getEndTime()   != null && b.getEndTime().isBefore(now))  continue;
            active.add(b);
        }
        return active;
    }

    /** banner 任意写操作后，失效 banner + 首页缓存（首页会带 banner） */
    private void invalidate() {
        cache.evictAll(CACHE_KEY_ACTIVE_BANNERS, CACHE_KEY_HOME_DATA);
    }
}
