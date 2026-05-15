package com.onlinecollege.index.service;

import com.onlinecollege.index.entity.EduBanner;

import java.util.List;

public interface BannerService {

    EduBanner add(EduBanner banner);

    boolean update(Long id, EduBanner banner);

    boolean delete(Long id);

    EduBanner getById(Long id);

    /** 后台：所有 banner（含禁用、已过期），分页/排序留给前端 */
    List<EduBanner> listAll();

    /** C 端：当前生效的 banner（带缓存） */
    List<EduBanner> listActiveWithCache();
}
