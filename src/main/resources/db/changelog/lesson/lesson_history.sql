--liquibase formatted sql

--changeset maxdrv:create_lesson_history_table
create table if not exists lesson_history
(
    id                bigint      primary key,
    created_at        timestamptz not null,
    updated_at        timestamptz not null,
    lesson_id         bigint      not null,
    status            text        not null,
    status_updated_at timestamptz not null,
    constraint lesson_history_fk_lesson foreign key (lesson_id) references lesson (id)
);

create sequence if not exists lesson_history_seq start 10000 increment 10;