package com.edu.ulab.app.mapper;

import com.edu.ulab.app.entity.Book;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BookJdbcMapper implements RowMapper<Book> {
    @Override
    public Book mapRow(ResultSet rs, int rowNum) throws SQLException {
        Book book = new Book();

        book.setId(rs.getInt("id"));
        book.setAuthor(rs.getString("AUTHOR"));
        book.setTitle(rs.getString("TITLE"));
        book.setPageCount(rs.getInt("PAGE_COUNT"));
        return book;
    }
}
