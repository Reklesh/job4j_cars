create table history_owner(
    id serial primary key,
    car_id int not null references car(id),
    owner_id int not null references owner(id),
    startAt TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    endAt TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);