package com.onlinecollege.file.excel;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;

/**
 * 课程分类 Excel 一行数据。
 *
 * <p>EasyExcel 按 {@code @ExcelProperty.index} 映射列：
 * <pre>
 *   列0 = 一级分类名称（必填）
 *   列1 = 二级分类名称（可选，留空表示本行只新增一级分类）
 *   列2 = 一级分类排序（可选，默认 0）
 *   列3 = 二级分类排序（可选，默认 0）
 * </pre>
 *
 * <p>示例（xlsx 表头不会被使用，按 index 读）：
 * <pre>
 * | 一级分类 | 二级分类 | 一级排序 | 二级排序 |
 * | 前端开发 | Vue     | 1       | 1       |
 * | 前端开发 | React   | 1       | 2       |
 * | 后端开发 | Java    | 2       | 1       |
 * </pre>
 */
@Data
public class SubjectExcelRow {

    @ExcelProperty(index = 0)
    private String firstLevelTitle;

    @ExcelProperty(index = 1)
    private String secondLevelTitle;

    @ExcelProperty(index = 2)
    private Integer firstLevelSort;

    @ExcelProperty(index = 3)
    private Integer secondLevelSort;
}
