--liquibase formatted sql

--changeset maxdrv:create_lesson_item_history_table
create table if not exists lesson_item_history
(
    id                bigint      primary key,
    created_at        timestamptz not null,
    updated_at        timestamptz not null,
    lesson_item_id    bigint      not null,
    status            text        not null,
    status_updated_at timestamptz not null,
    constraint lesson_item_history_fk_lesson_item foreign key (lesson_item_id) references lesson_item (id)
);

create sequence if not exists lesson_item_history_seq start 10000 increment 10;