package com.onlinecollege.book.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.onlinecollege.book.entity.Book;
import com.onlinecollege.book.service.BookService;
import com.onlinecollege.common.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 图书管理 REST 接口
 *
 * <p>直连本服务（context-path = {@code /book-service}）：
 * {@code http://localhost:8081/book-service/api/books/...}。
 *
 * <p>通过网关访问：{@code http://localhost:8080/api/books/...}。
 */
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "图书管理", description = "图书 CRUD 接口")
public class BookController {

    private final BookService bookService;

    @PostMapping
    @Operation(summary = "新增图书")
    public Result<Book> addBook(@Valid @RequestBody Book book) {
        return Result.success(bookService.addBook(book));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "根据 ID 删除图书")
    public Result<Boolean> deleteBook(
            @Parameter(description = "图书 ID", required = true)
            @PathVariable Long id) {
        return Result.success(bookService.deleteBookById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "根据 ID 更新图书")
    public Result<Boolean> updateBook(
            @Parameter(description = "图书 ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody Book book) {
        book.setId(id);
        return Result.success(bookService.updateBookById(book));
    }

    @GetMapping("/{id}")
    @Operation(summary = "根据 ID 查询图书")
    public Result<Book> getBook(
            @Parameter(description = "图书 ID", required = true)
            @PathVariable Long id) {
        return Result.success(bookService.getBookById(id));
    }

    @GetMapping("/page")
    @Operation(summary = "分页查询图书")
    public Result<Page<Book>> pageBooks(
            @Parameter(description = "页码，默认 1")
            @RequestParam(defaultValue = "1") Integer pageNum,
            @Parameter(description = "每页大小，默认 10，最大 100")
            @RequestParam(defaultValue = "10") Integer pageSize) {
        return Result.success(bookService.getBookPage(pageNum, pageSize));
    }

    @GetMapping("/list")
    @Operation(summary = "按条件查询图书列表")
    public Result<List<Book>> listBooks(
            @Parameter(description = "书名（模糊匹配）")
            @RequestParam(required = false) String bookName,
            @Parameter(description = "作者（精确匹配）")
            @RequestParam(required = false) String author) {
        return Result.success(bookService.getBookList(bookName, author));
    }
}
