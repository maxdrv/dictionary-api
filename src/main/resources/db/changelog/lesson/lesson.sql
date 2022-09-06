--liquibase formatted sql

--changeset maxdrv:create_lesson_table
create table if not exists lesson
(
    id             bigint      primary key,
    created_at     timestamptz not null,
    updated_at     timestamptz not null,
    start_at       timestamptz not null,
    status         text        not null,
    parent_plan_id bigint      not null,
    description    text        not null
);

create sequence if not exists lesson_seq start 10000 increment 10;