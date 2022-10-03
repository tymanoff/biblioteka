package com.edu.ulab.app.service.impl;

import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.entity.Person;
import com.edu.ulab.app.exception.NotFoundException;
import com.edu.ulab.app.mapper.UserMapper;
import com.edu.ulab.app.repository.UserRepository;
import com.edu.ulab.app.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserServiceImpl(UserRepository userRepository,
                           UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);

        Person savedUser = userRepository.save(user);
        log.info("Saved user: {}", savedUser);

        return userMapper.personToUserDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto) {
        Person user = userMapper.userDtoToPerson(userDto);
        log.info("Mapped user: {}", user);

        Person personSource = userRepository.findByIdForUpdate(user.getId())
                .orElseThrow(() -> new NotFoundException("No user with id: " + user.getId()));
        log.info("Check user in database: {}", personSource);

        mapperUserUpdate(user, personSource);
        log.info("Update user: {}", personSource);

        Person savedUser = userRepository.save(personSource);
        log.info("Saved user: {}", savedUser);

        return userMapper.personToUserDto(savedUser);
    }

    private void mapperUserUpdate(Person userUpdate, Person personSource) {
        personSource.setFullName(userUpdate.getFullName());
        personSource.setAge(userUpdate.getAge());
        personSource.setTitle(userUpdate.getTitle());
    }

    @Override
    public UserDto getUserById(Integer id) {
        Person person = userRepository
                .findById(id)
                .orElseThrow(() -> new NotFoundException("No user with id: " + id));
        log.info("User found with id : {}", id);

        return userMapper.personToUserDto(person);
    }

    @Override
    public void deleteUserById(Integer id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            log.info("Delete user with id: {}", id);
        } else {
            throw new NotFoundException("No user with id: " + id);
        }
    }
}
