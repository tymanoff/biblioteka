create table ulab_edu.book
(
    id         integer     not null,
    person_id  integer     not null,
    title      varchar(50) not null,
    author     varchar(50) not null,
    page_count integer     not null,
    constraint pk_ulab_edu_book_id primary key (id)
);

comment on table ulab_edu.book is 'Справочник используется для хранения баджей';
comment on column ulab_edu.book.id is 'Идентификатор книги';
comment on column ulab_edu.book.person_id is 'Идентификатор пользователя';
comment on column ulab_edu.book.title is 'Заголовок';
comment on column ulab_edu.book.author is 'Автор';
comment on column ulab_edu.book.page_count is 'Количество страниц';

