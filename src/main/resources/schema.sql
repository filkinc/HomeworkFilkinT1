create table if not exists tasks (
    id serial primary key,
    title varchar(100) not null,
    description text not null,
    user_id int
);