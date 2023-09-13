create table if not exists registration.place_images
(
    id               bigserial                        not null,
    place_id         bigserial                        not null,
    image_id         bigserial                        not null,

    primary key (id),
    foreign key (place_id) references registration.place(id),
    foreign key (image_id) references registration.image(id)
)