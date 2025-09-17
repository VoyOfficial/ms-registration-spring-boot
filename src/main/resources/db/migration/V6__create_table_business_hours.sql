create table if not exists registration.business_hours
(
    id             bigserial                       not null,
    weekday        varchar(15)                     not null,
    interval_id    bigserial                       not null,

    primary key (id),
    foreign key (interval_id) references registration.interval_hours(id)
)