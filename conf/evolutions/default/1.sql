# --- !Ups

set ignorecase true;

create table "cities" (
  "id" bigserial primary key,
  "name" varchar not null
);

create table "people" (
  "id" bigserial primary key,
  "name" varchar not null,
  "age" int not null,
  "middle_name" varchar,
  "phone" varchar,
  "city_id" bigint not null references cities(id)
);

# --- !Downs

drop table "people" if exists;
drop table "cities" if exists;
