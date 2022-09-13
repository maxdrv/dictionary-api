--liquibase formatted sql

--changeset maxdrv:create_user_table
create table if not exists api_user
(
    id                bigint      primary key,
    created_at        timestamptz not null,
    updated_at        timestamptz not null,
    username          text        not null,
    password          text        not null,
    email             text        not null,
    enabled           boolean     default false,
    created           timestamptz not null,
    current_lesson_id bigint
);

comment on column api_user.created_at is 'tech column';
comment on column api_user.created is 'creation date-time for user';

create sequence if not exists api_user_seq start 10000 increment 10;

--changeset maxdrv:insert_stab_user
insert into api_user (id, created_at, updated_at, username, password, email, enabled, created, current_lesson_id)
    values (1, now(), now(), 'username', 'password', 'email', true, now(), null);