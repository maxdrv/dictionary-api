--liquibase formatted sql

--changeset maxdrv:create_lesson_tag_mapping_table

create table if not exists lesson_tag_mapping
(
    lesson_id  bigint not null references lesson(id),
    tag_id     bigint   not null references tag(id),
    primary key (lesson_id, tag_id)
);