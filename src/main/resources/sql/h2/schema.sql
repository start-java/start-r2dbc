drop table if exists people;
create table people (
  id   varchar(36) primary key,
  name varchar(50) not null
);