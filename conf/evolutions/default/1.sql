# --- !Ups

create table "people" (
  "id" bigserial primary key,
  "name" varchar not null,
  "age" int not null,
  "middle_name" varchar,
  "city_id" bigint not null references cities(id)
);

create table "cities" (
  "id" bigserial primary key,
  "name" varchar not null,
);

# --- !Downs

drop table "people" if exists;
drop table "cities" if exists;
