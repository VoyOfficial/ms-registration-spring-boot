create table if not exists registration.image
(
    id               bigserial                        not null,
    image            varchar(300) unique               not null,

    primary key (id)
)