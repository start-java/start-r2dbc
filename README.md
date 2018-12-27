# Start R2DBC

For running unit test, need to :

1. Create a empty postgres database with name 'test' and owner to 'test' with login password 'password'
2. Run sql script file '[sql/postgres/schema-create.sql]'.
3. Run 'mvn test'.

[sql/postgres/schema-create.sql]: https://github.com/start-java/start-r2dbc/blob/master/src/main/resources/sql/postgres/schema.sql

## 1. [R2DBC]

R2DBC > Reactive Relational Database Connectivity

- [r2dbc-spi](https://github.com/r2dbc/r2dbc-spi)
- [r2dbc-spi-test](https://github.com/r2dbc/r2dbc-spi/tree/master/r2dbc-spi-test) -  a Technology Compatibility Kit (TCK)
- [r2dbc-client](https://github.com/r2dbc/r2dbc-client)
- Drivers：
    - [r2dbc-h2](https://github.com/r2dbc/r2dbc-h2)
    - [r2dbc-postgresql](https://github.com/r2dbc/r2dbc-postgresql)
    - [r2dbc-mssql](https://github.com/r2dbc/r2dbc-mssql)
    - [r2dbc-over-adba](https://github.com/r2dbc/r2dbc-over-adba)
- [r2dbc.github.io](https://github.com/r2dbc/r2dbc.github.io)
- Examples:
    - [r2dbc-postgresql example](https://github.com/r2dbc/r2dbc-postgresql/blob/master/src/test/java/io/r2dbc/postgresql/PostgresqlExample.java)

## 2. [spring-data-r2dbc]

- [spring-data-r2dbc](https://github.com/spring-projects/spring-data-r2dbc)
- [spring-data-jdbc/spring-data-relational](https://github.com/spring-projects/spring-data-jdbc/tree/master/spring-data-relational)
- Examples:
    - [spring-data-examples/r2dbc](https://github.com/spring-projects/spring-data-examples/tree/master/r2dbc)

## 3. Release Packages

| Date           | Name                      | Version      | Description
|----------------|---------------------------|--------------|-------------
| **2018-12-12** | spring-data-r2dbc         | **1.0.0.M1** | [link](https://repo.spring.io/milestone/org/springframework/data/spring-data-r2dbc/1.0.0.M1/)
| **2018-11-19** | io.r2dbc:r2dbc-spi        | **1.0.0.M6** | ↑ [released](https://r2dbc.io/2018/11/19/r2dbc-1-0-milestone-6-released)
| 2018-11-19     | io.r2dbc:r2dbc-spi        | 1.0.0.M6     | ↑
| 2018-11-19     | io.r2dbc:r2dbc-client     | 1.0.0.M6     | ↑
| 2018-11-19     | io.r2dbc:r2dbc-postgresql | 1.0.0.M6     | ↑
| 2018-11-19     | io.r2dbc:r2dbc-h2         | 1.0.0.M6     | + new driver
| 2018-11-19     | io.r2dbc:r2dbc-mssql      | 1.0.0.M6     | + new driver
| **2018-09-24** | io.r2dbc:r2dbc-spi        | **1.0.0.M5** | ↑
| 2018-09-24     | io.r2dbc:r2dbc-client     | 1.0.0.M5     | ↑
| 2018-09-24     | io.r2dbc:r2dbc-postgresql | 1.0.0.M5     | ↑

## 4. Maven Repositories

- <https://repo.spring.io/milestone/io/r2dbc>
- <https://repo.spring.io/milestone/org/springframework/data>
- <https://repo.spring.io/snapshot/org/springframework/data/spring-data-r2dbc>
- <https://repo.spring.io/snapshot/org/springframework/data/spring-data-jdbc>

## 5. Ref

- 2018-12-19 spring blog : [Spring Tips: Reactive SQL Data Access with Spring Data R2DBC](https://spring.io/blog/2018/12/19/spring-tips-reactive-sql-data-access-with-spring-data-r2dbc)
- 2018-12-12 spring blog : [Spring Data R2DBC 1.0 M1 released](https://spring.io/blog/2018/12/12/spring-data-r2dbc-1-0-m1-released)
- 2018-10-01 infoQ : [Experimental Reactive Relational Database Connectivity Driver, R2DBC, Announced at SpringOne](https://www.infoq.com/news/2018/10/springone-r2dbc)
- 2018-09-27 spring blog : [The Reactive Revolution at SpringOne Platform 2018 (part 1/N)](https://spring.io/blog/2018/09/27/the-reactive-revolution-at-springone-platform-2018-part-1-n)
- spring-guides : [Accessing Relational Data using JDBC with Spring](https://spring.io/guides/gs/relational-data-access/)


[R2DBC]: https://r2dbc.io
[spring-data-r2dbc]: https://github.com/spring-projects/spring-data-r2dbc