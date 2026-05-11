package com.onlinecollege.index.service;

import com.onlinecollege.index.vo.HomeDataVo;

public interface HomeService {

    /** 取首页聚合数据（带 Redis 缓存） */
    HomeDataVo getHomeData();

    /** 管理端主动失效首页缓存（课程发布/下架后可调用） */
    void invalidate();
}
