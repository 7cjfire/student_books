package com.onlinecollege.file.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 阿里云 VOD（视频点播）配置。
 *
 * <p>获取上传凭证 / 播放凭证属于 OpenAPI 调用，仅需 AccessKey 即可；视频上传由前端
 * 拿到凭证后直传，不经过本服务以免带宽成为瓶颈。
 *
 * <p>关掉时（{@link #enabled} = {@code false}）相关接口会直接抛 {@code BusinessException}，
 * 不会让应用启动失败，方便没有 VOD AccessKey 的本地开发。
 */
@Data
@Component
@ConfigurationProperties(prefix = "vod")
public class VodProperties {

    /** 是否启用 VOD；false 时凭证接口会拒绝服务 */
    private boolean enabled = false;

    /** VOD 接入区域 ID，例如 {@code cn-shanghai}、{@code cn-beijing} */
    private String regionId = "cn-shanghai";

    /** 上传凭证 AccessKey（建议用独立子账号，只授予 VOD 权限） */
    private String accessKeyId;

    private String accessKeySecret;

    /** 获取播放凭证时的有效期（秒，最大 3000，默认 3000） */
    private long playAuthTimeout = 3000L;

    /** 上传凭证是否走 HTTPS 协议 */
    private boolean uploadHttps = true;

    /** 默认分类 ID（阿里云 VOD 里可配），0 表示不归类 */
    private long defaultCateId = 0L;
}
