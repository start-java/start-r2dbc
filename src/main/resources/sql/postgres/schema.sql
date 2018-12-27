drop table if exists dream;
create table dream (
  id        int primary key,
  name      varchar(50) not null,
  create_on timestamp
);