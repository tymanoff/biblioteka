package com.edu.ulab.app.service;


import com.edu.ulab.app.dto.BookDto;

public interface BookService {
    BookDto createBook(BookDto bookDto);

    BookDto updateBook(BookDto bookDto);

    BookDto getBookById(Integer id);

    void deleteBookById(Integer id);

    Iterable<BookDto> getAllBooks();
}
