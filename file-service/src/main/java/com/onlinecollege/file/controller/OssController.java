package com.onlinecollege.file.controller;

import com.onlinecollege.common.Result;
import com.onlinecollege.common.exception.BusinessException;
import com.onlinecollege.file.config.OssProperties;
import com.onlinecollege.file.service.SubjectImportService;
import com.onlinecollege.file.service.SubjectImportService.ImportResult;
import com.onlinecollege.file.storage.FileStorage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Set;

/**
 * OSS 上传接口
 *
 * <p>两个主要入口：
 * <ul>
 *   <li>{@code POST /upload/avatar}          — 头像（图片类型）上传</li>
 *   <li>{@code POST /upload/course-catalog}  — 课程分类 Excel 上传并写入 {@code edu_subject} 表</li>
 * </ul>
 *
 * <p>直连路径（含 context-path）：{@code http://localhost:8086/file-service/upload/...}
 */
@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
@Tag(name = "文件上传", description = "头像/Excel 上传到阿里云 OSS 或本地磁盘")
public class OssController {

    /** 允许的头像扩展名（小写） */
    private static final Set<String> AVATAR_EXTENSIONS = Set.of("jpg", "jpeg", "png", "gif", "webp");
    private static final long MAX_AVATAR_SIZE = 5L * 1024 * 1024;   // 5MB
    private static final Set<String> EXCEL_EXTENSIONS = Set.of("xls", "xlsx");
    private static final long MAX_EXCEL_SIZE = 10L * 1024 * 1024;   // 10MB

    private final FileStorage fileStorage;
    private final OssProperties properties;
    private final SubjectImportService subjectImportService;

    @PostMapping("/avatar")
    @Operation(summary = "上传头像", description = "仅允许 jpg/jpeg/png/gif/webp，最大 5MB")
    public Result<String> uploadAvatar(@RequestParam("file") MultipartFile file) {
        checkFile(file, AVATAR_EXTENSIONS, MAX_AVATAR_SIZE, "头像");
        String url = fileStorage.upload(file, properties.getAvatarDir());
        return Result.success(url);
    }

    @PostMapping("/course-catalog")
    @Operation(summary = "上传课程分类 Excel",
            description = "xlsx/xls 文件；上传到 OSS 后同时解析并写入 edu_subject 表")
    public Result<ImportResult> uploadCourseCatalog(@RequestParam("file") MultipartFile file) {
        checkFile(file, EXCEL_EXTENSIONS, MAX_EXCEL_SIZE, "Excel");

        // 先把原文件存一份，便于后续审计 / 下载
        String url = fileStorage.upload(file, properties.getCourseCatalogDir());

        // 再解析内容写库
        ImportResult imported = subjectImportService.importFromExcel(file);
        imported.setFileUrl(url);
        return Result.success(imported);
    }

    private static void checkFile(MultipartFile file, Set<String> allowedExt, long maxSize, String label) {
        if (file == null || file.isEmpty()) {
            throw BusinessException.badRequest(label + "文件不能为空");
        }
        if (file.getSize() > maxSize) {
            throw new BusinessException(413, label + "文件大小超过限制 " + (maxSize / 1024 / 1024) + "MB");
        }
        String original = file.getOriginalFilename();
        if (!StringUtils.hasText(original) || !original.contains(".")) {
            throw BusinessException.badRequest(label + "文件名缺少扩展名");
        }
        String ext = original.substring(original.lastIndexOf('.') + 1).toLowerCase();
        if (!allowedExt.contains(ext)) {
            throw BusinessException.badRequest(label + "仅支持 " + allowedExt + " 扩展名，当前：" + ext);
        }
    }
}
