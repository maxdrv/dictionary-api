--liquibase formatted sql

--changeset maxdrv:create_lesson_item_table
create table if not exists lesson_item
(
    id               bigint      primary key,
    created_at       timestamptz not null,
    updated_at       timestamptz not null,
    status           text        not null,
    lesson_id        bigint      not null,
    parent_phrase_id bigint      not null,
    question         text        not null,
    answer_correct   text        not null,
    answer_user      text,
    constraint lesson_item_fk_lesson foreign key (lesson_id) references lesson (id)
);

create sequence if not exists lesson_item_seq start 10000 increment 10;