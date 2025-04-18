create table history(
    id serial primary key,
    startAt TIMESTAMP WITHOUT TIME ZONE DEFAULT now(),
    endAt TIMESTAMP WITHOUT TIME ZONE DEFAULT now()
);