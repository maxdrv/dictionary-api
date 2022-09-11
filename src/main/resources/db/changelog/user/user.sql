--liquibase formatted sql

--changeset maxdrv:create_user_table
create table if not exists api_user
(
    id                bigint      primary key,
    created_at        timestamptz not null,
    updated_at        timestamptz not null,
    current_lesson_id bigint
);

create sequence if not exists api_user_seq start 10000 increment 10;

--changeset maxdrv:insert_stab_user
insert into api_user (id, created_at, updated_at, current_lesson_id) values (1, now(), now(), null);