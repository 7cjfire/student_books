package com.onlinecollege.book.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.onlinecollege.book.entity.Book;
import com.onlinecollege.book.mapper.BookMapper;
import com.onlinecollege.book.service.BookService;
import com.onlinecollege.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.List;

/**
 * 图书服务实现
 */
@Slf4j
@Service
public class BookServiceImpl extends ServiceImpl<BookMapper, Book> implements BookService {

    private static final int DEFAULT_PAGE_NUM = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;

    @Override
    public Book addBook(Book book) {
        // 清理前端传入的只读字段
        book.setId(null);

        LocalDate now = LocalDate.now();
        book.setCreateTime(now);
        book.setUpdateTime(now);

        if (book.getStock() == null) {
            book.setStock(0);
        }

        this.save(book);
        log.info("新增图书成功: id={}, bookName={}", book.getId(), book.getBookName());
        return book;
    }

    @Override
    public boolean deleteBookById(Long id) {
        if (id == null) {
            throw BusinessException.badRequest("图书 ID 不能为空");
        }
        if (this.getById(id) == null) {
            throw BusinessException.notFound("图书不存在，ID: " + id);
        }
        return this.removeById(id);
    }

    @Override
    public boolean updateBookById(Book book) {
        if (book.getId() == null) {
            throw BusinessException.badRequest("图书 ID 不能为空");
        }
        if (this.getById(book.getId()) == null) {
            throw BusinessException.notFound("图书不存在，ID: " + book.getId());
        }

        // 不允许通过更新接口覆盖创建时间
        book.setCreateTime(null);
        book.setUpdateTime(LocalDate.now());
        return this.updateById(book);
    }

    @Override
    public Book getBookById(Long id) {
        if (id == null) {
            throw BusinessException.badRequest("图书 ID 不能为空");
        }
        Book book = this.getById(id);
        if (book == null) {
            throw BusinessException.notFound("图书不存在，ID: " + id);
        }
        return book;
    }

    @Override
    public Page<Book> getBookPage(Integer pageNum, Integer pageSize) {
        int safePageNum = (pageNum == null || pageNum < 1) ? DEFAULT_PAGE_NUM : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? DEFAULT_PAGE_SIZE : pageSize;
        if (safePageSize > MAX_PAGE_SIZE) {
            safePageSize = MAX_PAGE_SIZE;
        }

        Page<Book> page = new Page<>(safePageNum, safePageSize);
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<Book>()
                .orderByDesc(Book::getId);
        return this.page(page, wrapper);
    }

    @Override
    public List<Book> getBookList(String bookName, String author) {
        LambdaQueryWrapper<Book> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(bookName)) {
            wrapper.like(Book::getBookName, bookName);
        }
        if (StringUtils.hasText(author)) {
            wrapper.eq(Book::getAuthor, author);
        }
        wrapper.orderByDesc(Book::getId);
        return this.list(wrapper);
    }
}
