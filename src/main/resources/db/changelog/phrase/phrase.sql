--liquibase formatted sql

--changeset maxdrv:create_phrase_table
create table if not exists phrase
(
    id            bigint      primary key,
    created_at    timestamptz not null,
    updated_at    timestamptz not null,
    lesson_id     bigint              ,
    source        text        not null,
    source_lang   text        not null,
    transcription text        not null,
    target        text        not null,
    target_lang   text        not null
);

create sequence if not exists phrase_seq start 10000 increment 10;