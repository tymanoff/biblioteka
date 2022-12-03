package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.BookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;

    private final BookMapper bookMapper;

    public BookServiceImpl(BookRepository bookRepository,
                           BookMapper bookMapper) {
        this.bookRepository = bookRepository;
        this.bookMapper = bookMapper;
    }

    @Override
    public BookDto createBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);
        Book savedBook = bookRepository.save(book);
        log.info("Saved book: {}", savedBook);
        return bookMapper.bookToBookDto(savedBook);
    }

    @Override
    public BookDto updateBook(BookDto bookDto) {
        Book book = bookMapper.bookDtoToBook(bookDto);
        log.info("Mapped book: {}", book);

        Book bookSource = bookRepository.findByIdForUpdate(book.getId())
                .orElseThrow(() -> new NotFoundException("No book with id: " + book.getId()));

        mapperBookUpdate(book, bookSource);
        log.debug("Update book: {}", bookSource);

        Book savedBook = bookRepository.save(bookSource);
        log.info("Saved book: {}", savedBook);

        return bookMapper.bookToBookDto(savedBook);
    }

    private void mapperBookUpdate(Book book, Book bookSource) {
        bookSource.setAuthor(book.getAuthor());
        bookSource.setTitle(book.getTitle());
        bookSource.setPageCount(book.getPageCount());
    }

    @Override
    public BookDto getBookById(Integer id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("No book with id: " + id));
        log.info("Book with id: {}", id);
        return bookMapper.bookToBookDto(book);
    }

    @Override
    public void deleteBookById(Integer id) {
        if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id);
            log.info("Delete book with id: {}", id);
        } else {
            throw new NotFoundException("No book with id: " + id);
        }
    }

    @Override
    public List<BookDto> getAllBooks() {
        log.info("Get all Books.");
        return bookMapper.booksToBookDtos(bookRepository.findAll());
    }
}
