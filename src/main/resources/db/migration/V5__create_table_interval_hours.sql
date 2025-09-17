create table if not exists registration.interval_hours
(
    id             bigserial                       not null,
    start_time     varchar(10)                      not null,
    end_time       varchar(10)                      not null,

    primary key (id)
)