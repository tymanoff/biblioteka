package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.repository.BookRepository;
import com.edu.ulab.app.service.impl.BookServiceImpl;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.BookServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing book functionality.")
public class BookServiceImplTest {
    @InjectMocks
    BookServiceImpl bookService;

    @Mock
    BookRepository bookRepository;

    @Mock
    BookMapper bookMapper;

    Person person;
    BookDto bookDto;
    BookDto result;
    Book book;
    Book savedBook;

    @BeforeEach
    void setup() {
        person = new Person();
        person.setId(1);

        bookDto = new BookDto();
        bookDto.setUserId(1);
        bookDto.setAuthor("test author");
        bookDto.setTitle("test title");
        bookDto.setPageCount(1000);

        result = new BookDto();
        result.setId(1);
        result.setUserId(1);
        result.setAuthor("test author");
        result.setTitle("test title");
        result.setPageCount(1000);

        book = new Book();
        book.setPageCount(1000);
        book.setTitle("test title");
        book.setAuthor("test author");
        book.setPerson(person);

        savedBook = new Book();
        savedBook.setId(1);
        savedBook.setPageCount(1000);
        savedBook.setTitle("test title");
        savedBook.setAuthor("test author");
        savedBook.setPerson(person);
    }

    @Test
    @DisplayName("Создание книги. Должно пройти успешно.")
    void saveBook_Test() {
        //given

        //when
        when(bookMapper.bookDtoToBook(bookDto)).thenReturn(book);
        when(bookRepository.save(book)).thenReturn(savedBook);
        when(bookMapper.bookToBookDto(savedBook)).thenReturn(result);

        //then
        BookDto bookDtoResult = bookService.createBook(bookDto);
        Assertions.assertThat(1).isEqualTo(bookDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление книги. Должно пройти успешно.")
    void updateBook_Test() {
        //given
        BookDto bookDtoUpdate = new BookDto();
        bookDtoUpdate.setId(11);
        bookDtoUpdate.setUserId(1);
        bookDtoUpdate.setAuthor("test author1");
        bookDtoUpdate.setTitle("test title1");
        bookDtoUpdate.setPageCount(10001);

        BookDto resultUpdate = new BookDto();
        resultUpdate.setId(11);
        resultUpdate.setUserId(1);
        resultUpdate.setAuthor("test author1");
        resultUpdate.setTitle("test title1");
        resultUpdate.setPageCount(10001);

        Book bookUpdate = new Book();
        bookUpdate.setId(11);
        bookUpdate.setPageCount(10001);
        bookUpdate.setTitle("test title1");
        bookUpdate.setAuthor("test author1");
        bookUpdate.setPerson(person);

        Book savedUpdateBook = new Book();
        savedUpdateBook.setId(11);
        savedUpdateBook.setPageCount(10001);
        savedUpdateBook.setTitle("test title1");
        savedUpdateBook.setAuthor("test author1");
        savedUpdateBook.setPerson(person);

        //when
        Mockito.when(bookMapper.bookDtoToBook(any(BookDto.class))).thenReturn(bookUpdate);
        Mockito.when(bookRepository.findByIdForUpdate(anyInt())).thenReturn(Optional.ofNullable(savedBook));
        Mockito.when(bookRepository.save(any(Book.class))).thenReturn(savedUpdateBook);
        Mockito.when(bookMapper.bookToBookDto(any(Book.class))).thenReturn(resultUpdate);

        //then
        BookDto bookDtoResult = bookService.updateBook(bookDtoUpdate);
        Assertions.assertThat(bookDtoResult).isEqualTo(resultUpdate);
    }

    @Test
    @DisplayName("Получение книги. Должно пройти успешно.")
    void getBook_Test() {
        //given
        BookDto resultGetBook = new BookDto();
        resultGetBook.setId(1);
        resultGetBook.setUserId(1);
        resultGetBook.setAuthor("test author");
        resultGetBook.setTitle("test title");
        resultGetBook.setPageCount(1000);

        //when
        Mockito.when(bookRepository.findById(anyInt())).thenReturn(Optional.ofNullable(savedBook));
        Mockito.when(bookMapper.bookToBookDto(any(Book.class))).thenReturn(resultGetBook);

        //then
        BookDto bookDtoResult = bookService.getBookById(savedBook.getId());
        Assertions.assertThat(bookDtoResult).isEqualTo(result);
    }

    @Test
    @DisplayName("Удаление книги. Должно пройти успешно.")
    void deleteBook_Test() {
        //given
        int id = 1;

        //when
        bookService.deleteBookById(id);

        //then
        Mockito.verify(bookRepository).deleteById(id);
    }

    @Test
    @DisplayName("Получение всех книг. Должно пройти успешно.")
    void getAllBooks_Test() {
        //given
        List<BookDto> listBook = new ArrayList<>();

        //when
        Mockito.when(bookMapper.booksToBookDtos(any())).thenReturn(listBook);

        //then
        List<BookDto> allBooksResult = bookService.getAllBooks();
        Assertions.assertThat(allBooksResult).isEqualTo(listBook);
    }

    @Test
    @DisplayName("Не найден book по id при обновлении.")
    void willThrowWhenUpdateBookNotFound_Test() {
        //given
        BookDto bookDtoUpdate = new BookDto();
        bookDtoUpdate.setId(11);
        bookDtoUpdate.setUserId(1);
        bookDtoUpdate.setAuthor("test author1");
        bookDtoUpdate.setTitle("test title1");
        bookDtoUpdate.setPageCount(10001);

        Book bookUpdate = new Book();
        bookUpdate.setId(11);
        bookUpdate.setPageCount(10001);
        bookUpdate.setTitle("test title1");
        bookUpdate.setAuthor("test author1");
        bookUpdate.setPerson(person);

        //when
        Mockito.when(bookMapper.bookDtoToBook(any(BookDto.class))).thenReturn(bookUpdate);

        //then
        Assertions.assertThatThrownBy(() -> bookService.updateBook(bookDtoUpdate))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No book with id: " + bookDtoUpdate.getId());
    }

    @Test
    @DisplayName("Не найдена книга по id.")
    void willThrowWhenGetBookNotFound_Test() {
        //given
        int id = 1;
        BDDMockito.given(bookRepository.findById(id)).willReturn(Optional.empty());

        //when
        //then
        Assertions.assertThatThrownBy(() -> bookService.getBookById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No book with id: " + id);
    }
}
