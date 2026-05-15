package com.onlinecollege.file.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.read.listener.ReadListener;
import com.onlinecollege.file.entity.EduSubject;
import com.onlinecollege.file.mapper.EduSubjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * EasyExcel 一行一行解析课程分类 Excel 的监听器。
 *
 * <p>解析规则：
 * <ul>
 *   <li>列 0：一级分类名称（必填）— 同名只会插入一次</li>
 *   <li>列 1：二级分类名称（可选）— 归属到同一行的一级分类下</li>
 * </ul>
 *
 * <p>该 Listener 是 <b>非单例</b>，每次导入 new 一个；通过构造器传入 Mapper（不能用 Spring 注入）。
 */
@Slf4j
public class SubjectExcelListener implements ReadListener<SubjectExcelRow> {

    /** 已落库的一级分类：title -> id，避免重复插入 */
    private final Map<String, Long> firstLevelCache = new HashMap<>();

    private final EduSubjectMapper mapper;

    private int totalRows = 0;
    private int firstLevelInserted = 0;
    private int secondLevelInserted = 0;

    public SubjectExcelListener(EduSubjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void invoke(SubjectExcelRow row, AnalysisContext context) {
        totalRows++;

        if (!StringUtils.hasText(row.getFirstLevelTitle())) {
            log.warn("第 {} 行一级分类为空，跳过", context.readRowHolder().getRowIndex());
            return;
        }

        String firstTitle = row.getFirstLevelTitle().trim();
        Long firstId = firstLevelCache.get(firstTitle);
        if (firstId == null) {
            EduSubject first = new EduSubject();
            first.setTitle(firstTitle);
            first.setParentId(0L);
            first.setSort(row.getFirstLevelSort() == null ? 0 : row.getFirstLevelSort());
            mapper.insert(first);
            firstId = first.getId();
            firstLevelCache.put(firstTitle, firstId);
            firstLevelInserted++;
        }

        if (StringUtils.hasText(row.getSecondLevelTitle())) {
            EduSubject second = new EduSubject();
            second.setTitle(row.getSecondLevelTitle().trim());
            second.setParentId(firstId);
            second.setSort(row.getSecondLevelSort() == null ? 0 : row.getSecondLevelSort());
            mapper.insert(second);
            secondLevelInserted++;
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        log.info("Excel 解析完成，共 {} 行，插入一级 {} 条，插入二级 {} 条",
                totalRows, firstLevelInserted, secondLevelInserted);
    }

    public int getTotalRows() {
        return totalRows;
    }

    public int getFirstLevelInserted() {
        return firstLevelInserted;
    }

    public int getSecondLevelInserted() {
        return secondLevelInserted;
    }
}
