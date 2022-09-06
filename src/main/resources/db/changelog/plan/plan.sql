--liquibase formatted sql

--changeset maxdrv:create_plan_table
create table if not exists plan
(
    id            bigint      primary key,
    created_at    timestamptz not null,
    updated_at    timestamptz not null,
    description   text        not null
);

create sequence if not exists plan_seq start 10000 increment 10;