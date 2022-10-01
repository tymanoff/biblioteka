package com.edu.ulab.app.mapper;

import com.edu.ulab.app.dto.BookDto;
import com.edu.ulab.app.entity.Book;
import com.edu.ulab.app.web.request.BookRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface BookMapper {

    BookDto bookRequestToBookDto(BookRequest bookRequest);

    Book bookDtoToBook(BookDto bookDto);

    List<BookDto> bookRequestToBookDto(List<BookRequest> bookUpdateRequest);

    BookDto bookToBookDto(Book book);

    List<BookDto> booksToBookDtos(Iterable<Book> books);

    BookDto ObjectTOBookDto(Object object);
}
