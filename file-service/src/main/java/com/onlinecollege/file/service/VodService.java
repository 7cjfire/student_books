package com.onlinecollege.file.service;

import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.vod.model.v20170321.CreateUploadVideoRequest;
import com.aliyuncs.vod.model.v20170321.CreateUploadVideoResponse;
import com.aliyuncs.vod.model.v20170321.DeleteVideoRequest;
import com.aliyuncs.vod.model.v20170321.DeleteVideoResponse;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthRequest;
import com.aliyuncs.vod.model.v20170321.GetVideoPlayAuthResponse;
import com.aliyuncs.vod.model.v20170321.RefreshUploadVideoRequest;
import com.aliyuncs.vod.model.v20170321.RefreshUploadVideoResponse;
import com.onlinecollege.common.exception.BusinessException;
import com.onlinecollege.file.config.VodProperties;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * 阿里云 VOD OpenAPI 封装：仅处理"获取上传凭证 / 刷新凭证 / 获取播放凭证 / 删除视频"。
 *
 * <p>实际视频数据由前端拿到 {@code uploadAddress + uploadAuth} 后，
 * 直传到 VOD（或底层 OSS），全程不经过本服务，节省带宽。
 */
@Slf4j
@Service
public class VodService {

    private final VodProperties properties;
    private volatile DefaultAcsClient acsClient;

    @Autowired
    public VodService(VodProperties properties) {
        this.properties = properties;
    }

    /** 申请视频上传凭证 */
    public UploadAuthResult createUploadVideo(String title, String fileName, String description, Long cateId) {
        ensureEnabled();
        if (!StringUtils.hasText(title)) {
            throw BusinessException.badRequest("视频标题不能为空");
        }
        if (!StringUtils.hasText(fileName)) {
            throw BusinessException.badRequest("文件名不能为空");
        }

        CreateUploadVideoRequest request = new CreateUploadVideoRequest();
        request.setTitle(title);
        request.setFileName(fileName);
        if (StringUtils.hasText(description)) {
            request.setDescription(description);
        }
        long realCateId = cateId != null ? cateId : properties.getDefaultCateId();
        if (realCateId > 0) {
            request.setCateId(realCateId);
        }
        try {
            CreateUploadVideoResponse response = client().getAcsResponse(request);
            UploadAuthResult result = new UploadAuthResult();
            result.setVideoId(response.getVideoId());
            result.setUploadAddress(response.getUploadAddress());
            result.setUploadAuth(response.getUploadAuth());
            result.setRequestId(response.getRequestId());
            log.info("VOD 上传凭证申请成功: videoId={}, title={}", result.getVideoId(), title);
            return result;
        } catch (ClientException e) {
            log.error("VOD 上传凭证申请失败 title={}", title, e);
            throw new BusinessException(500, "VOD 上传凭证申请失败: " + e.getErrMsg());
        }
    }

    /** 当上传凭证过期（默认 3000 秒），用 videoId 刷新 */
    public UploadAuthResult refreshUploadVideo(String videoId) {
        ensureEnabled();
        if (!StringUtils.hasText(videoId)) {
            throw BusinessException.badRequest("videoId 不能为空");
        }
        RefreshUploadVideoRequest request = new RefreshUploadVideoRequest();
        request.setVideoId(videoId);
        try {
            RefreshUploadVideoResponse response = client().getAcsResponse(request);
            UploadAuthResult result = new UploadAuthResult();
            result.setVideoId(response.getVideoId());
            result.setUploadAddress(response.getUploadAddress());
            result.setUploadAuth(response.getUploadAuth());
            result.setRequestId(response.getRequestId());
            return result;
        } catch (ClientException e) {
            log.error("VOD 上传凭证刷新失败 videoId={}", videoId, e);
            throw new BusinessException(500, "VOD 上传凭证刷新失败: " + e.getErrMsg());
        }
    }

    /** 获取视频播放凭证（前端播放器用） */
    public PlayAuthResult getVideoPlayAuth(String videoId) {
        ensureEnabled();
        if (!StringUtils.hasText(videoId)) {
            throw BusinessException.badRequest("videoId 不能为空");
        }
        GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
        request.setVideoId(videoId);
        request.setAuthInfoTimeout(properties.getPlayAuthTimeout());
        try {
            GetVideoPlayAuthResponse response = client().getAcsResponse(request);
            PlayAuthResult result = new PlayAuthResult();
            result.setVideoId(videoId);
            result.setPlayAuth(response.getPlayAuth());
            result.setTitle(response.getVideoMeta() != null ? response.getVideoMeta().getTitle() : null);
            result.setCoverURL(response.getVideoMeta() != null ? response.getVideoMeta().getCoverURL() : null);
            result.setDuration(response.getVideoMeta() != null ? response.getVideoMeta().getDuration() : null);
            return result;
        } catch (ClientException e) {
            log.error("VOD 播放凭证获取失败 videoId={}", videoId, e);
            throw new BusinessException(500, "VOD 播放凭证获取失败: " + e.getErrMsg());
        }
    }

    /** 删除 VOD 视频（课程章节删除时可调用） */
    public void deleteVideo(String videoIds) {
        ensureEnabled();
        if (!StringUtils.hasText(videoIds)) {
            return;
        }
        DeleteVideoRequest request = new DeleteVideoRequest();
        request.setVideoIds(videoIds);
        try {
            DeleteVideoResponse response = client().getAcsResponse(request);
            log.info("VOD 删除视频成功: videoIds={}, requestId={}", videoIds, response.getRequestId());
        } catch (ClientException e) {
            log.warn("VOD 删除视频失败 videoIds={}, err={}", videoIds, e.getErrMsg());
            // 删除失败只告警，不阻塞业务流程
        }
    }

    private void ensureEnabled() {
        if (!properties.isEnabled()) {
            throw new BusinessException(503,
                    "VOD 未启用：请配置 vod.enabled=true 及有效的 AccessKey");
        }
        if (!StringUtils.hasText(properties.getAccessKeyId())
                || !StringUtils.hasText(properties.getAccessKeySecret())) {
            throw new BusinessException(503,
                    "VOD AccessKey 未配置，请设置环境变量 VOD_ACCESS_KEY_ID / VOD_ACCESS_KEY_SECRET");
        }
    }

    /** 懒加载 AcsClient，避免未启用 VOD 时强行初始化失败 */
    private DefaultAcsClient client() {
        DefaultAcsClient local = this.acsClient;
        if (local == null) {
            synchronized (this) {
                local = this.acsClient;
                if (local == null) {
                    DefaultProfile profile = DefaultProfile.getProfile(
                            properties.getRegionId(),
                            properties.getAccessKeyId(),
                            properties.getAccessKeySecret());
                    local = new DefaultAcsClient(profile);
                    this.acsClient = local;
                }
            }
        }
        return local;
    }

    @Data
    public static class UploadAuthResult {
        private String videoId;
        /** Base64 编码的上传地址 */
        private String uploadAddress;
        /** Base64 编码的上传凭证 */
        private String uploadAuth;
        private String requestId;
    }

    @Data
    public static class PlayAuthResult {
        private String videoId;
        private String playAuth;
        private String title;
        private String coverURL;
        private Integer duration;
    }
}
