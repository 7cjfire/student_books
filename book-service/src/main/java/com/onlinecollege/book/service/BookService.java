package com.onlinecollege.book.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.onlinecollege.book.entity.Book;

import java.util.List;

/**
 * 图书服务接口
 */
public interface BookService extends IService<Book> {

    /**
     * 新增图书
     *
     * @param book 图书信息
     * @return 新增后的图书（含自增 ID）
     */
    Book addBook(Book book);

    /**
     * 根据 ID 删除图书
     *
     * @param id 图书 ID
     * @return 是否成功
     */
    boolean deleteBookById(Long id);

    /**
     * 根据 ID 更新图书
     *
     * @param book 图书信息
     * @return 是否成功
     */
    boolean updateBookById(Book book);

    /**
     * 根据 ID 查询图书（查不到抛 {@code BusinessException}）
     *
     * @param id 图书 ID
     * @return 图书
     */
    Book getBookById(Long id);

    /**
     * 分页查询图书
     *
     * @param pageNum  页码（从 1 开始）
     * @param pageSize 每页大小
     * @return 分页结果
     */
    Page<Book> getBookPage(Integer pageNum, Integer pageSize);

    /**
     * 按条件查询图书列表
     *
     * @param bookName 书名（模糊匹配，可空）
     * @param author   作者（精确匹配，可空）
     * @return 图书列表
     */
    List<Book> getBookList(String bookName, String author);
}
