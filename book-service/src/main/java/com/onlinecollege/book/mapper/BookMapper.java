package com.onlinecollege.book.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.onlinecollege.book.entity.Book;
import org.apache.ibatis.annotations.Mapper;

/**
 * 图书 Mapper 接口
 */
@Mapper
public interface BookMapper extends BaseMapper<Book> {
}
