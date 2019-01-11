drop table if exists dream;
drop table if exists people;

create table dream (
  id        int auto_increment primary key,
  name      varchar(50) not null,
  create_on timestamp
);

create table people (
  id   varchar(36) primary key,
  name varchar(50) not null
);