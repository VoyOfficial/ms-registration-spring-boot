create table if not exists registration.user
(
    id             bigserial                        not null,
    name           varchar(30)                      not null,
    surname        varchar(50)                      not null,
    phone          varchar(14)                      not null,
    date_birth     timestamp                        not null,
    sex            registration.sex_enum            not null,
    marital_status registration.marital_status_enum not null,
    city           varchar(30)                      not null,
    state          varchar(2)                       not null,
    cpf            varchar(11) unique               not null,
    occupation     varchar(30)                      not null,

    primary key (id)
)