--liquibase formatted sql

--changeset maxdrv:create_tag_table
create table if not exists tag
(
    id         bigint      primary key,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    key        text        not null
);

create sequence if not exists tag_seq start 10000 increment 10;

create unique index uq_tag_key on tag (key);