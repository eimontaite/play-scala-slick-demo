# --- Sample dataset

# --- !Ups

insert into cities (id,name) values (  1,'Vilnius');
insert into cities (id,name) values (  2,'Jonava');
insert into cities (id,name) values (  3,'Palanga');

# --- !Downs

delete from cities;