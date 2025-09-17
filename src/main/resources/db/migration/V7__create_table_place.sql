create table if not exists registration.place
(
	id                         bigserial                        not null,
	document                   varchar(20) unique               not null,
	name                       varchar(50)                      not null,
	about                      varchar(250)                     not null,
	contact                    varchar(20)                      not null,
	address                    varchar(100)                     not null,
	rating                     decimal                          not null,
	isSaved                    bool                             not null,
	userRatingsTotal           decimal                          not null,
	distanceOfLocal            decimal                          not null,
	state                      varchar(2)                       not null,
	city                       varchar(30)                      not null,
	business_hours_id          bigserial                        not null,

	primary key (id),

	foreign key (business_hours_id) references registration.business_hours(id)
)