--liquibase formatted sql

--changeset maxdrv:create_refresh_token_table
create table if not exists refresh_token
(
    id         bigint      primary key,
    created_at timestamptz not null,
    updated_at timestamptz not null,
    token      text        not null,
    created    timestamptz not null
);

comment on column refresh_token.created_at is 'tech column';
comment on column refresh_token.created is 'used for refresh token';

create sequence if not exists refresh_token_seq start 10000 increment 10;