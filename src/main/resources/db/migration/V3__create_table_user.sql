create table if not exists voyregistration.user
(
    id             bigserial           not null,
    name           varchar(100)        not null,
    phone          varchar(14)         not null,
    date_birth     timestamp           not null,
    merital_status merital_status_enum not null,
    genre          varchar(14)         not null,
    city           varchar(30)         not null,
    state          varchar(2)          not null,
    cpf            varchar(11) unique  not null,
    occupation     varchar(30)         not null,

    primary key (id)
)