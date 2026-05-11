package com.onlinecollege.book.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 图书实体
 * 对应数据库表 {@code book}
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("book")
@Schema(description = "图书实体")
public class Book {

    @TableId(value = "id", type = IdType.AUTO)
    @Schema(description = "图书ID", example = "1")
    private Long id;

    @NotBlank(message = "书名不能为空")
    @Size(max = 100, message = "书名长度不能超过 100")
    @Schema(description = "书名", example = "Java编程思想", required = true)
    private String bookName;

    @NotBlank(message = "作者不能为空")
    @Size(max = 50, message = "作者长度不能超过 50")
    @Schema(description = "作者", example = "Bruce Eckel", required = true)
    private String author;

    @Size(max = 100, message = "出版社长度不能超过 100")
    @Schema(description = "出版社", example = "机械工业出版社")
    private String publisher;

    @Schema(description = "出版日期", example = "2023-01-01")
    private LocalDate publishDate;

    @DecimalMin(value = "0.00", message = "价格不能为负数")
    @Schema(description = "价格", example = "89.50")
    private BigDecimal price;

    @PositiveOrZero(message = "库存不能为负数")
    @Schema(description = "库存", example = "100")
    private Integer stock;

    @Schema(description = "创建时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate createTime;

    @Schema(description = "更新时间", accessMode = Schema.AccessMode.READ_ONLY)
    private LocalDate updateTime;
}
