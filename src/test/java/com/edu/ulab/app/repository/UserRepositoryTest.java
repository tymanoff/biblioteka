package com.edu.ulab.app.repository;

import com.edu.ulab.app.config.SystemJpaTest;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.vladmihalcea.sql.SQLStatementCountValidator;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.jdbc.Sql;

import java.util.Optional;

/**
 * Тесты репозитория {@link UserRepository}.
 */
@SystemJpaTest
public class UserRepositoryTest {
    @Autowired
    UserRepository userRepository;

    @BeforeEach
    void setUp() {
        SQLStatementCountValidator.reset();
    }

    @DisplayName("Сохранить юзера. Число select должно равняться 1. Число Insert должно равняться 1.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void insertPerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(123);
        person.setTitle("reader123");
        person.setFullName("Test123");

        //When
        Person result = userRepository.save(person);

        //Then
        Assertions.assertThat(userRepository.count()).isEqualTo(2);
        Assertions.assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(person);
        SQLStatementCountValidator.assertSelectCount(1);
        SQLStatementCountValidator.assertInsertCount(1);
        SQLStatementCountValidator.assertUpdateCount(0);
        SQLStatementCountValidator.assertDeleteCount(0);
    }

    @DisplayName("Обновить юзера. Число select должно равняться 1. Число Insert должно равняться 1. Число Update должно равняться 1.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void updatePerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(123);
        person.setTitle("reader123");
        person.setFullName("Test123");

        Person personUpdate = new Person();
        personUpdate.setAge(321);
        personUpdate.setTitle("123reader");
        personUpdate.setFullName("123Test");

        Person savePerson = userRepository.save(person);

        //When
        mapperUpdatePerson(personUpdate, savePerson);
        Person result = userRepository.save(savePerson);

        //Then
        Assertions.assertThat(userRepository.count()).isEqualTo(2);
        Assertions.assertThat(result)
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(personUpdate);
        SQLStatementCountValidator.assertSelectCount(1);
        SQLStatementCountValidator.assertInsertCount(1);
        SQLStatementCountValidator.assertUpdateCount(1);
        SQLStatementCountValidator.assertDeleteCount(0);
    }

    private void mapperUpdatePerson(Person personUpdate, Person savePerson) {
        savePerson.setAge(personUpdate.getAge());
        savePerson.setTitle(personUpdate.getTitle());
        savePerson.setFullName(personUpdate.getFullName());
    }

    @DisplayName("Получить юзера. Число select должно равняться 1. Число Insert должно равняться 1.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void getPerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(123);
        person.setTitle("reader123");
        person.setFullName("Test123");

        Person savePerson = userRepository.save(person);

        //When
        Optional<Person> result = userRepository.findById(savePerson.getId());

        //Then
        Assertions.assertThat(userRepository.count()).isEqualTo(2);
        Assertions.assertThat(result.get())
                .usingRecursiveComparison()
                .ignoringFields("id")
                .isEqualTo(person);
        SQLStatementCountValidator.assertSelectCount(1);
        SQLStatementCountValidator.assertInsertCount(1);
        SQLStatementCountValidator.assertUpdateCount(0);
        SQLStatementCountValidator.assertDeleteCount(0);
    }

    @DisplayName("Удалить юзера. Число select должно равняться 1. Число Insert должно равняться 1. Число delete должно равняться 1.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void deletePerson_thenAssertDmlCount() {
        //Given
        Person person = new Person();
        person.setAge(123);
        person.setTitle("reader123");
        person.setFullName("Test123");

        Person result = userRepository.save(person);

        //When
        userRepository.deleteById(result.getId());

        //Then
        Assertions.assertThat(userRepository.count()).isEqualTo(1);
        SQLStatementCountValidator.assertSelectCount(1);
        SQLStatementCountValidator.assertInsertCount(1);
        SQLStatementCountValidator.assertUpdateCount(0);
        SQLStatementCountValidator.assertDeleteCount(1);
    }

    @DisplayName("При сохранении юзера выбрасывается исключение, когда в параметр передаётся null.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void willThrowWhenSavePersonNotFound_thenAssertDmlCount() {
        //given
        //When
        //Then
        Assertions.assertThatThrownBy(() -> userRepository.save(null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
        Assertions.assertThat(userRepository.count()).isEqualTo(1);
        SQLStatementCountValidator.assertSelectCount(1);
        SQLStatementCountValidator.assertInsertCount(0);
        SQLStatementCountValidator.assertUpdateCount(0);
        SQLStatementCountValidator.assertDeleteCount(0);
    }

    @DisplayName("При получении несуществующего юзера выбрасывается исключение.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void willThrowWhenGetPersonNotFound_thenAssertDmlCount() {
        //given
        int id = 11;
        //When
        //Then
        Assertions.assertThatThrownBy(() -> userRepository.findById(id)
                        .orElseThrow(() -> new NotFoundException("No user with id: " + id)))
                .isInstanceOf(NotFoundException.class);
        Assertions.assertThat(userRepository.count()).isEqualTo(1);
        SQLStatementCountValidator.assertSelectCount(2);
        SQLStatementCountValidator.assertInsertCount(0);
        SQLStatementCountValidator.assertUpdateCount(0);
        SQLStatementCountValidator.assertDeleteCount(0);
    }

    @DisplayName("При удалении юзера выбрасывается исключение, когда в параметр передаётся null.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void willThrowWhenDeletePersonIsNull_thenAssertDmlCount() {
        //Given
        //When
        //Then
        Assertions.assertThatThrownBy(() -> userRepository.deleteById(null))
                .isInstanceOf(InvalidDataAccessApiUsageException.class);
        SQLStatementCountValidator.assertSelectCount(0);
        SQLStatementCountValidator.assertInsertCount(0);
        SQLStatementCountValidator.assertUpdateCount(0);
        SQLStatementCountValidator.assertDeleteCount(0);
    }

    @DisplayName("При удалении несуществующего юзера выбрасывается исключение.")
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
        Assertions.assertThatThrownBy(() -> userRepository.deleteById(id))
                .isInstanceOf(EmptyResultDataAccessException.class);
        SQLStatementCountValidator.assertSelectCount(1);
        SQLStatementCountValidator.assertInsertCount(0);
        SQLStatementCountValidator.assertUpdateCount(0);
        SQLStatementCountValidator.assertDeleteCount(0);
    }

    @DisplayName("При сохранении юзера выбрасывается исключение, когда у юзера Title равен null.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void willThrowWhenSavePersonTitleIsNull_thenAssertDmlCount() {
        //given
        Person person = new Person();
        person.setAge(123);
        person.setTitle(null);
        person.setFullName("Test123");

        //When
        //Then
        Assertions.assertThat(userRepository.count()).isEqualTo(1);
        Assertions.assertThatThrownBy(() -> userRepository.save(person))
                .isInstanceOf(DataIntegrityViolationException.class);
        SQLStatementCountValidator.assertSelectCount(2);
        SQLStatementCountValidator.assertInsertCount(0);
        SQLStatementCountValidator.assertUpdateCount(0);
        SQLStatementCountValidator.assertDeleteCount(0);
    }

    @DisplayName("При сохранении юзера выбрасывается исключение, когда у юзера FullName равен null.")
    @Test
    @Rollback
    @Sql({"classpath:sql/1_clear_schema.sql",
            "classpath:sql/2_insert_person_data.sql",
            "classpath:sql/3_insert_book_data.sql"
    })
    void willThrowWhenSavePersonFullNameIsNull_thenAssertDmlCount() {
        //given
        Person person = new Person();
        person.setAge(123);
        person.setTitle("reader");
        person.setFullName(null);

        //When
        //Then
        Assertions.assertThat(userRepository.count()).isEqualTo(1);
        Assertions.assertThatThrownBy(() -> userRepository.save(person))
                .isInstanceOf(DataIntegrityViolationException.class);
        SQLStatementCountValidator.assertSelectCount(1);
        SQLStatementCountValidator.assertInsertCount(0);
        SQLStatementCountValidator.assertUpdateCount(0);
        SQLStatementCountValidator.assertDeleteCount(0);
    }
}
