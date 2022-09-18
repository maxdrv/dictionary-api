--liquibase formatted sql

--changeset maxdrv:create_authority_table
create table if not exists authority
(
    id         bigint      primary key,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    type       text        not null,
    user_id    bigint      not null
);

create sequence if not exists authority_seq start 10000 increment 10;

--changeset maxdrv:insert_authority_for_stub_users
insert into authority (id, created_at, updated_at, type, user_id) values
        (1, now(), now(), 'USER', 1),
        (2, now(), now(), 'EDITOR', 2),
        (3, now(), now(), 'ADMIN', 3);
