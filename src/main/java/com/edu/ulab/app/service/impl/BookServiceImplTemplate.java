package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookJdbcMapper;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookServiceImplTemplate implements BookService {

    private final JdbcTemplate jdbcTemplate;
    private final BookMapper bookMapper;

    @Override
    public BookDto createBook(BookDto bookDto) {
        final String INSERT_SQL = "INSERT INTO BOOK(TITLE, AUTHOR, PAGE_COUNT, USER_ID) VALUES (?,?,?,?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(
                new PreparedStatementCreator() {
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps =
                                connection.prepareStatement(INSERT_SQL, new String[]{"id"});
                        ps.setString(1, bookDto.getTitle());
                        ps.setString(2, bookDto.getAuthor());
                        ps.setLong(3, bookDto.getPageCount());
                        ps.setLong(4, bookDto.getUserId());
                        return ps;
                    }
                },
                keyHolder);

        bookDto.setId(Objects.requireNonNull(keyHolder.getKey()).intValue());
        return bookDto;
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        final String UPDATE_SQL = "UPDATE BOOK SET TITLE = ?, AUTHOR = ?, PAGE_COUNT = ? WHERE id = ?";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(UPDATE_SQL, new String[]{"id"});
                    ps.setString(1, bookDto.getTitle());
                    ps.setString(2, bookDto.getAuthor());
                    ps.setLong(3, bookDto.getPageCount());
                    ps.setLong(4, bookDto.getId());
                    return ps;
                });
        log.debug("Update book: {}", bookDto);

        return bookDto;
    }

    @Override
    public BookDto getBookById(Integer id) {
        final String GET_SQL = "SELECT * FROM BOOK WHERE id = ?";
        Book book = jdbcTemplate.query(
                        connection -> {
                            PreparedStatement ps = connection.prepareStatement(GET_SQL, new String[]{"id"});
                            ps.setLong(1, id);
                            return ps;
                        }, new BookJdbcMapper()).stream()
                .findAny()
                .orElseThrow(() -> new NotFoundException("No book with id: " + id));
        log.info("Book with id: {}", id);

        return bookMapper.ObjectTOBookDto(book);
    }

    @Override
    public void deleteBookById(Integer id) {
        final String DELETE_SQL = "DELETE FROM BOOK WHERE id = ?";
        jdbcTemplate.update(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(DELETE_SQL, new String[]{"id"});
                    ps.setLong(1, id);
                    return ps;
                });
        log.info("Delete book with id: {}", id);
    }

    @Override
    public List<BookDto> getAllBooks() {
        final String GET_ALL_BOOK_SQL = "SELECT * FROM BOOK";
        List<Book> bookList = jdbcTemplate.query(
                connection -> {
                    PreparedStatement ps = connection.prepareStatement(GET_ALL_BOOK_SQL);
                    return ps;
                }, new BookJdbcMapper());
        log.info("Get all Books.");

        return bookMapper.booksToBookDtos(bookList);
    }
}
