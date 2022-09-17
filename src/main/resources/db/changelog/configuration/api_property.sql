--liquibase formatted sql

--changeset maxdrv:create_api_property_table
create table if not exists api_property
(
    id             bigint      primary key,
    created_at     timestamptz not null,
    updated_at     timestamptz not null,
    key            text        not null,
    value          text
);

create sequence if not exists api_property_seq start 10000 increment 10;