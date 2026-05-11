package com.onlinecollege.file.service;

import com.alibaba.excel.EasyExcel;
import com.onlinecollege.common.exception.BusinessException;
import com.onlinecollege.file.excel.SubjectExcelListener;
import com.onlinecollege.file.excel.SubjectExcelRow;
import com.onlinecollege.file.mapper.EduSubjectMapper;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * 课程分类 Excel 导入服务
 *
 * <p>读取 {@link MultipartFile} 的输入流，由 EasyExcel 逐行推送给
 * {@link SubjectExcelListener}，Listener 在遇到一级分类时做"存在即复用"去重，
 * 遇到二级分类时挂到同一行的一级分类下。
 *
 * <p>整个过程在一个事务里，Excel 解析出错会回滚已经写入的分类。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectImportService {

    private final EduSubjectMapper eduSubjectMapper;

    @Transactional(rollbackFor = Exception.class)
    public ImportResult importFromExcel(MultipartFile file) {
        SubjectExcelListener listener = new SubjectExcelListener(eduSubjectMapper);
        try (InputStream in = file.getInputStream()) {
            EasyExcel.read(in, SubjectExcelRow.class, listener)
                    // 跳过第一行表头
                    .headRowNumber(1)
                    .sheet()
                    .doRead();
        } catch (IOException e) {
            log.error("读取 Excel 失败 filename={}", file.getOriginalFilename(), e);
            throw new BusinessException(500, "读取 Excel 失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("解析 Excel 失败 filename={}", file.getOriginalFilename(), e);
            throw new BusinessException(500, "解析 Excel 失败: " + e.getMessage());
        }

        ImportResult result = new ImportResult();
        result.setOriginalFilename(file.getOriginalFilename());
        result.setTotalRows(listener.getTotalRows());
        result.setFirstLevelInserted(listener.getFirstLevelInserted());
        result.setSecondLevelInserted(listener.getSecondLevelInserted());
        return result;
    }

    @Data
    public static class ImportResult {
        private String originalFilename;
        private String fileUrl;
        private int totalRows;
        private int firstLevelInserted;
        private int secondLevelInserted;
    }
}
