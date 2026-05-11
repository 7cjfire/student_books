package com.onlinecollege.index.service.impl;

import com.onlinecollege.index.cache.HotDataCache;
import com.onlinecollege.index.config.IndexCacheProperties;
import com.onlinecollege.index.mapper.IndexCourseMapper;
import com.onlinecollege.index.mapper.IndexTeacherMapper;
import com.onlinecollege.index.service.BannerService;
import com.onlinecollege.index.service.HomeService;
import com.onlinecollege.index.vo.HomeDataVo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

/**
 * 首页数据服务：聚合 banner / 热门课程 / 最新课程 / 推荐讲师，整体走 Redis 缓存。
 *
 * <p>粒度决策：整体缓存一个大对象，不做细粒度 key，是因为首页查询非常高频且改动低频（N 分钟 TTL），
 * 细粒度反而让 Redis 往返次数更多。写入侧发生 banner 变更或课程发布时主动 evict 即可。
 */
@Service
@RequiredArgsConstructor
public class HomeServiceImpl implements HomeService {

    private static final int HOT_LIMIT = 8;
    private static final int LATEST_LIMIT = 8;
    private static final int TEACHER_LIMIT = 6;

    private final HotDataCache cache;
    private final IndexCacheProperties cacheProps;
    private final BannerService bannerService;
    private final IndexCourseMapper courseMapper;
    private final IndexTeacherMapper teacherMapper;

    @Override
    public HomeDataVo getHomeData() {
        Duration ttl = Duration.ofSeconds(cacheProps.getHomeTtlSeconds());
        return cache.getOrLoad(BannerServiceImpl.CACHE_KEY_HOME_DATA, ttl, this::buildFromDb);
    }

    @Override
    public void invalidate() {
        cache.evict(BannerServiceImpl.CACHE_KEY_HOME_DATA);
    }

    private HomeDataVo buildFromDb() {
        HomeDataVo vo = new HomeDataVo();
        // banner 单独走自己的缓存，首页这里直接取
        vo.setBanners(bannerService.listActiveWithCache());
        vo.setHotCourses(courseMapper.selectHotCourses(HOT_LIMIT));
        vo.setLatestCourses(courseMapper.selectLatestCourses(LATEST_LIMIT));
        vo.setRecommendedTeachers(teacherMapper.selectRecommendedTeachers(TEACHER_LIMIT));
        return vo;
    }
}
