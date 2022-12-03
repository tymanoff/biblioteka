package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

/**
 * Тесты репозитория {@link BookRepository}.
 */
@SystemJpaTest
public class BookRepositoryTest {
    @Autowired
    BookRepository bookRepository;
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить книгу и автора. Число select должно равняться 1. Число Insert должно равняться 2.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void findAllBadges_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(123);
        person.setTitle("reader123");
        person.setFullName("Test Test");

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author123");
        book.setTitle("test123");
        book.setPageCount(111);
        book.setPerson(savedPerson);

        //When
        Book result = bookRepository.save(book);

        //Then
        Assertions.assertThat(bookRepository.count()).isEqualTo(3);
        Assertions.assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(book);
        SQLStatementCountValidator.assertSelectCount(1);
        SQLStatementCountValidator.assertInsertCount(2);
        SQLStatementCountValidator.assertUpdateCount(0);
        SQLStatementCountValidator.assertDeleteCount(0);
    }

    @DisplayName("Обновить книгу. Число select должно равняться 3. Число Insert должно равняться 2. Число update должно равняться 1.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updateBook_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(123);
        person.setTitle("reader123");
        person.setFullName("Test Test");

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author123");
        book.setTitle("test123");
        book.setPageCount(100);
        book.setPerson(savedPerson);

        Book bookUpdate = new Book();
        bookUpdate.setAuthor("111Test");
        bookUpdate.setTitle("111test");
        bookUpdate.setPageCount(1111);

        Book saveBook = bookRepository.save(book);

        //When
        mapperUpdateBook(bookUpdate, saveBook);
        Book result = bookRepository.save(saveBook);

        //Then
        Assertions.assertThat(bookRepository.count()).isEqualTo(3);
        Assertions.assertThat(result)
                .usingRecursiveComparison()
                .ignoringExpectedNullFields()
                .ignoringFields("id", "Person")
                .isEqualTo(bookUpdate);
        SQLStatementCountValidator.assertSelectCount(3);
        SQLStatementCountValidator.assertInsertCount(2);
        SQLStatementCountValidator.assertUpdateCount(1);
        SQLStatementCountValidator.assertDeleteCount(0);
    }

    private void mapperUpdateBook(Book bookUpdate, Book saveBook) {
        saveBook.setTitle(bookUpdate.getTitle());
        saveBook.setAuthor(bookUpdate.getAuthor());
        saveBook.setPageCount(bookUpdate.getPageCount());
    }

    @DisplayName("Получить книгу и автора. Число select должно равняться 1. Число Insert должно равняться 2.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getAllBadges_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(123);
        person.setTitle("reader123");
        person.setFullName("Test Test");

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author123");
        book.setTitle("test123");
        book.setPageCount(111);
        book.setPerson(savedPerson);

        Book saveBook = bookRepository.save(book);

        //When
        Optional<Book> result = bookRepository.findById(saveBook.getId());

        //Then
        Assertions.assertThat(bookRepository.count()).isEqualTo(3);
        Assertions.assertThat(result.get())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(book);
        Assertions.assertThat(result.get().getPerson())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(person);
        SQLStatementCountValidator.assertSelectCount(1);
        SQLStatementCountValidator.assertInsertCount(2);
        SQLStatementCountValidator.assertUpdateCount(0);
        SQLStatementCountValidator.assertDeleteCount(0);
    }

    @DisplayName("Получить все книги. Число select должно равняться 2. Число Insert должно равняться 2.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getAllBook_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(123);
        person.setTitle("reader123");
        person.setFullName("Test Test");

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author123");
        book.setTitle("test123");
        book.setPageCount(111);
        book.setPerson(savedPerson);

        bookRepository.save(book);

        //When
        List<Book> allBook = bookRepository.findAll();

        //Then
        Assertions.assertThat(bookRepository.count()).isEqualTo(3);
        Assertions.assertThat(allBook.size()).isEqualTo(3);
        SQLStatementCountValidator.assertSelectCount(2);
        SQLStatementCountValidator.assertInsertCount(2);
        SQLStatementCountValidator.assertUpdateCount(0);
        SQLStatementCountValidator.assertDeleteCount(0);
    }

    @DisplayName("Удалить книгу. Число select должно равняться 1. Число Insert должно равняться 2. Число delete должно равняться 1.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deleteBook_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(123);
        person.setTitle("reader123");
        person.setFullName("Test Test");

        Person savedPerson = userRepository.save(person);

        Book book = new Book();
        book.setAuthor("Test Author123");
        book.setTitle("test123");
        book.setPageCount(111);
        book.setPerson(savedPerson);

        Book saveBook = bookRepository.save(book);

        //When
        bookRepository.deleteById(saveBook.getId());

        //Then
        Assertions.assertThat(bookRepository.count()).isEqualTo(2);
        SQLStatementCountValidator.assertSelectCount(1);
        SQLStatementCountValidator.assertInsertCount(2);
        SQLStatementCountValidator.assertUpdateCount(0);
        SQLStatementCountValidator.assertDeleteCount(1);
    }

    @DisplayName("При получении несуществующей книги выбрасывается исключение.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void willThrowWhenGetPersonNotFound_thenAssertDmlCount() {
        //Given
        int id = 11;
        //When
        //Then
        Assertions.assertThatThrownBy(() -> bookRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("No book with id: " + id)))
                .isInstanceOf(NotFoundException.class);
        SQLStatementCountValidator.assertSelectCount(1);
        SQLStatementCountValidator.assertInsertCount(0);
        SQLStatementCountValidator.assertUpdateCount(0);
        SQLStatementCountValidator.assertDeleteCount(0);
    }

    @DisplayName("При удалении книги выбрасывается исключение.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void willThrowWhenDeletePersonNotFound_thenAssertDmlCount() {
        //Given
        int id = 5;
        //When
        //Then
        Assertions.assertThatThrownBy(() -> bookRepository.deleteById(id))
                .isInstanceOf(EmptyResultDataAccessException.class);
        SQLStatementCountValidator.assertSelectCount(1);
        SQLStatementCountValidator.assertInsertCount(0);
        SQLStatementCountValidator.assertUpdateCount(0);
        SQLStatementCountValidator.assertDeleteCount(0);
    }
}
