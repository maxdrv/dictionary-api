--liquibase formatted sql

--changeset maxdrv:create_demo_table

create table if not exists demo
(
    id   bigint primary key,
    name text   not null,
    type text   not null
);

create sequence if not exists demo_seq start 10000 increment 10;