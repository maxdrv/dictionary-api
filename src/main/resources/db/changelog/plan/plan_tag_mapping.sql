--liquibase formatted sql

--changeset maxdrv:create_plan_tag_mapping_table

create table if not exists plan_tag_mapping
(
    plan_id  bigint not null references plan(id),
    tag_id     bigint   not null references tag(id),
    primary key (plan_id, tag_id)
);