package com.onlinecollege.file.storage;

import org.springframework.web.multipart.MultipartFile;

/**
 * 统一文件存储抽象。当前有两种实现：
 * <ul>
 *   <li>{@code OssFileStorage} — 真正上传到阿里云 OSS</li>
 *   <li>{@code LocalFileStorage} — 存到本地磁盘（本地联调 / 没有 AccessKey 时的兜底）</li>
 * </ul>
 */
public interface FileStorage {

    /**
     * 上传文件到指定子目录。
     *
     * @param file 待上传的 Multipart 文件
     * @param dir  子目录（如 {@code "avatar/"}），自动以 / 结尾
     * @return 可访问的完整 URL
     */
    String upload(MultipartFile file, String dir);

    /**
     * 根据对象 key 删除文件。对象 key = dir + 重命名后的文件名。
     *
     * @param objectKey OSS 对象 key（本地存储时为相对路径）
     * @return 是否成功
     */
    boolean delete(String objectKey);
}
