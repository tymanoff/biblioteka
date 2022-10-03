package com.edu.ulab.app.service;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.impl.UserServiceImpl;
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

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

/**
 * Тестирование функционала {@link com.edu.ulab.app.service.impl.UserServiceImpl}.
 */
@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@DisplayName("Testing user functionality.")
public class UserServiceImplTest {
    @InjectMocks
    UserServiceImpl userService;

    @Mock
    UserRepository userRepository;

    @Mock
    UserMapper userMapper;

    UserDto userDto;
    Person person;
    Person savedPerson;
    UserDto result;

    @BeforeEach
    void setup() {
        userDto = new UserDto();
        userDto.setAge(11);
        userDto.setFullName("test name");
        userDto.setTitle("test title");

        person = new Person();
        person.setFullName("test name");
        person.setAge(11);
        person.setTitle("test title");

        savedPerson = new Person();
        savedPerson.setId(1);
        savedPerson.setFullName("test name");
        savedPerson.setAge(11);
        savedPerson.setTitle("test title");

        result = new UserDto();
        result.setId(1);
        result.setAge(11);
        result.setFullName("test name");
        result.setTitle("test title");
    }

    @Test
    @DisplayName("Создание пользователя. Должно пройти успешно.")
    void savePerson_Test() {
        //given

        //when
        Mockito.when(userMapper.userDtoToPerson(userDto)).thenReturn(person);
        Mockito.when(userRepository.save(person)).thenReturn(savedPerson);
        Mockito.when(userMapper.personToUserDto(savedPerson)).thenReturn(result);


        //then
        UserDto userDtoResult = userService.createUser(userDto);
        Assertions.assertThat(1).isEqualTo(userDtoResult.getId());
    }

    @Test
    @DisplayName("Обновление пользователя. Должно пройти успешно.")
    void updatePerson_Test() {
        //given
        UserDto userDtoUpdate = new UserDto();
        userDtoUpdate.setId(1);
        userDtoUpdate.setAge(111);
        userDtoUpdate.setFullName("test name1");
        userDtoUpdate.setTitle("test title1");

        Person personUpdate = new Person();
        personUpdate.setId(1);
        personUpdate.setFullName("test name1");
        personUpdate.setAge(111);
        personUpdate.setTitle("test title1");

        Person savedUpdatePerson = new Person();
        savedUpdatePerson.setId(1);
        savedUpdatePerson.setFullName("test name1");
        savedUpdatePerson.setAge(111);
        savedUpdatePerson.setTitle("test title1");

        UserDto resultUpdate = new UserDto();
        resultUpdate.setId(1);
        resultUpdate.setAge(111);
        resultUpdate.setFullName("test name1");
        resultUpdate.setTitle("test title1");

        //when
        Mockito.when(userMapper.userDtoToPerson(any(UserDto.class))).thenReturn(personUpdate);
        Mockito.when(userRepository.findByIdForUpdate(anyInt())).thenReturn(Optional.ofNullable(savedPerson));
        Mockito.when(userRepository.save(any(Person.class))).thenReturn(savedUpdatePerson);
        Mockito.when(userMapper.personToUserDto(any(Person.class))).thenReturn(resultUpdate);

        //then
        UserDto userDtoResult = userService.updateUser(userDtoUpdate);
        Assertions.assertThat(resultUpdate).isEqualTo(userDtoResult);
    }

    @Test
    @DisplayName("Получить пользователя. Должно пройти успешно.")
    void getPerson_Test() {
        //given
        UserDto resultUserDto = new UserDto();
        resultUserDto.setId(1);
        resultUserDto.setAge(11);
        resultUserDto.setFullName("test name");
        resultUserDto.setTitle("test title");

        //when
        Mockito.when(userRepository.findById(anyInt())).thenReturn(Optional.ofNullable(savedPerson));
        Mockito.when(userMapper.personToUserDto(any(Person.class))).thenReturn(resultUserDto);

        //then
        UserDto userDtoResult = userService.getUserById(savedPerson.getId());
        Assertions.assertThat(userDtoResult).isEqualTo(result);
    }

    @Test
    @DisplayName("Удалить пользователя. Должно пройти успешно.")
    void deletePerson_Test() {
        //given
        userService.createUser(userDto);

        //when

        userService.deleteUserById(savedPerson.getId());

        //then
        Mockito.verify(userRepository).deleteById(savedPerson.getId());
    }

    @Test
    @DisplayName("Не найден user по id при обновлении.")
    void willThrowWhenUpdatePersonNotFound_Test() {
        //given
        UserDto userDtoUpdate = new UserDto();
        userDtoUpdate.setId(1);
        userDtoUpdate.setAge(111);
        userDtoUpdate.setFullName("test name1");
        userDtoUpdate.setTitle("test title1");

        Person personUpdate = new Person();
        personUpdate.setId(1);
        personUpdate.setFullName("test name1");
        personUpdate.setAge(111);
        personUpdate.setTitle("test title1");

        //when
        Mockito.when(userMapper.userDtoToPerson(any(UserDto.class))).thenReturn(personUpdate);

        //then
        Assertions.assertThatThrownBy(() -> userService.updateUser(userDtoUpdate))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with id: " + userDtoUpdate.getId());
    }

    @Test
    @DisplayName("Не найден user по id.")
    void willThrowWhenGetFailedPersonNotFound_Test() {
        //given
        int id = 1;
        BDDMockito.given(userRepository.findById(id)).willReturn(Optional.empty());

        //when
        //then
        Assertions.assertThatThrownBy(() -> userService.getUserById(id))
                .isInstanceOf(NotFoundException.class)
                .hasMessage("No user with id: " + id);
    }
}
