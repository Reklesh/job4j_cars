create table owner(
    id serial primary key,
    name varchar not null,
    user_id int references auto_user(id)
);