truncate table
    lesson_item_history,
    lesson_item,
    lesson_history,
    lesson,
    phrase,
    plan_tag_mapping,
    tag,
    plan,
    api_user;


alter sequence if exists lesson_item_history_seq restart 10000;
alter sequence if exists lesson_item_seq restart 10000;
alter sequence if exists lesson_history_seq restart 10000;
alter sequence if exists lesson_seq restart 10000;
alter sequence if exists phrase_seq restart 10000;
alter sequence if exists phrase_seq restart 10000;
alter sequence if exists tag_seq restart 10000;
alter sequence if exists plan_seq restart 10000;
alter sequence if exists api_user_seq restart 10000;

