package com.edu.ulab.app.facade;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.dto.UserDto;
import com.edu.ulab.app.mapper.BookMapper;
import com.edu.ulab.app.mapper.UserMapper;

import com.edu.ulab.app.service.impl.BookServiceImpl;
import com.edu.ulab.app.service.impl.BookServiceImplTemplate;
import com.edu.ulab.app.service.impl.UserServiceImpl;
import com.edu.ulab.app.service.impl.UserServiceImplTemplate;
import com.edu.ulab.app.web.request.UserBookRequest;
import com.edu.ulab.app.web.response.UserBookResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserDataFacade {
    private final UserServiceImpl userService;
    private final BookServiceImpl bookService;
    private final UserMapper userMapper;
    private final BookMapper bookMapper;

    public UserBookResponse createUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book create request: {}", userBookRequest);
        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        UserDto createdUser = userService.createUser(userDto);
        log.info("Created user: {}", createdUser);

        List<Integer> bookIdList = userBookRequest.getBookRequests()
                .stream()
                .filter(Objects::nonNull)
                .map(bookMapper::bookRequestToBookDto)
                .peek(bookDto -> bookDto.setUserId(createdUser.getId()))
                .peek(mappedBookDto -> log.info("mapped book: {}", mappedBookDto))
                .map(bookService::createBook)
                .peek(createdBook -> log.info("Created book: {}", createdBook))
                .map(BookDto::getId)
                .toList();
        log.info("Collected book ids: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(createdUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse updateUserWithBooks(UserBookRequest userBookRequest) {
        log.info("Got user book update request: {}", userBookRequest);

        UserDto userDto = userMapper.userRequestToUserDto(userBookRequest.getUserRequest());
        log.info("Mapped user request: {}", userDto);

        getUserWithBooks(userDto.getId());
        log.info("The user is in the database: {}", userDto);

        UserDto updateUser = userService.updateUser(userDto);
        log.info("Update user: {}", updateUser);

        List<BookDto> bookDtoList = bookMapper.bookRequestToBookDto(userBookRequest.getBookRequests());
        log.info("Mapped book request: {}", bookDtoList);

        bookDtoList.forEach(book -> {
            if (book.getId() == null) {
                book.setUserId(userDto.getId());
                log.info("Book set userId: {}", book);

                bookService.createBook(book);
                log.info("Book create in database: {}", book);
            } else {
                log.info("Check book in database: {}", book.getId());
                bookService.getBookById(book.getId());

                bookService.updateBook(book);
                log.info("Book update: {}", book);
            }
        });
        log.info("Update book: {}", bookDtoList);

        List<Integer> bookIdList = getAllBooksUser(userDto.getId());
        log.info("Collected update book ids: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(updateUser.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public UserBookResponse getUserWithBooks(Integer userId) {
        log.info("Got user book get request: {}", userId);

        UserDto userDto = userService.getUserById(userId);
        log.info("Got a user: {}", userDto);

        List<Integer> bookIdList = getAllBooksUser(userId);
        log.info("Collected book ids: {}", bookIdList);

        return UserBookResponse.builder()
                .userId(userDto.getId())
                .booksIdList(bookIdList)
                .build();
    }

    public void deleteUserWithBooks(Integer userId) {
        log.info("Got user and book delete request: {}", userId);

        getUserWithBooks(userId);
        log.info("The user is in the database: {}", userId);

        userService.deleteUserById(userId);
        log.info("Deleted user");

        bookService.getAllBooks()
                .stream()
                .filter(Objects::nonNull)
                .filter(b -> b.getUserId().equals(userId))
                .forEach(b -> bookService.deleteBookById(b.getId()));
        log.info("Deleted user's books.");
    }

    private List<Integer> getAllBooksUser(Integer id) {
        log.info("Get all user's books.");
        return bookService.getAllBooks()
                .stream()
                .filter(Objects::nonNull)
                .filter(b -> b.getUserId().equals(id))
                .map(BookDto::getId)
                .toList();
    }
}
