# Start R2DBC

## 1. Unit test config

### 1.1. h2 - `mvn test -P h2`

h2 auto run in memory mode. Nothing need to config.

### 1.2. postgres - `mvn test -P postgres`

Before run unit test, need to config a database with:

- databaseName = test_r2dbc
- username = test
- password = password
- host = localhost
- port = 5432

Note : All default value could be changed by maven `-D` argument. Such as:

```
$ mvn test -P postgres \
    -D db.name=test_r2dbc \
    -D db.username=test \
    -D db.password=password \
    -D db.host=localhost \
    -D db.port=5432
````

## 2. [R2DBC] Resources

R2DBC > Reactive Relational Database Connectivity

- [r2dbc website](https://r2dbc.io) - [source on github](https://github.com/r2dbc/r2dbc.github.io)
- [r2dbc blog](https://r2dbc.io/blog)
- R2DBC Specification: [1.0.0.M7](https://r2dbc.io/spec/1.0.0.M7/spec/html)
- Core Repository :
    - [r2dbc-spi](https://github.com/r2dbc/r2dbc-spi)
    - [r2dbc-spi-test](https://github.com/r2dbc/r2dbc-spi/tree/master/r2dbc-spi-test) -  a Technology Compatibility Kit (TCK)
    - [r2dbc-bom](https://github.com/r2dbc/r2dbc-bom)
- Client Repository:
    - [r2dbc-client](https://github.com/r2dbc/r2dbc-client) - R2DBC Reactor Client
    - [spring-data-r2dbc](https://github.com/spring-projects/spring-data-r2dbc) - Spring Data R2DBC
- Driver Repository：
    - [r2dbc-h2](https://github.com/r2dbc/r2dbc-h2)
    - [r2dbc-postgresql](https://github.com/r2dbc/r2dbc-postgresql)
    - [r2dbc-mssql](https://github.com/r2dbc/r2dbc-mssql)
    - [r2dbc-proxy](https://github.com/r2dbc/r2dbc-proxy)
    - [r2dbc-over-adba](https://github.com/r2dbc/r2dbc-over-adba)
- Examples:
    - [r2dbc h2 example](https://github.com/r2dbc/r2dbc-h2/blob/master/src/test/java/io/r2dbc/h2/H2Example.java)
    - [r2dbc postgresql example](https://github.com/r2dbc/r2dbc-postgresql/blob/master/src/test/java/io/r2dbc/postgresql/PostgresqlExample.java)
    - [r2dbc mssql unit test](https://github.com/r2dbc/r2dbc-mssql/tree/master/src/test/java/io/r2dbc/mssql)
    - [spring-data-examples/r2dbc](https://github.com/spring-projects/spring-data-examples/tree/master/r2dbc)
- Maven Repositories :
    - <https://repo.spring.io/milestone/io/r2dbc>
    - <https://repo.spring.io/snapshot/org/springframework/data/spring-data-r2dbc>
    - <https://repo.spring.io/milestone/org/springframework/data>
    - <https://repo.spring.io/snapshot/org/springframework/data/spring-data-jdbc>
- Others :
    - [spring-data-jdbc/spring-data-relational](https://github.com/spring-projects/spring-data-jdbc/tree/master/spring-data-relational)

## 3. Release Packages

| Date           | Name                      | Version      | Description
|----------------|---------------------------|--------------|-------------
| 2018-12-12     | spring-data-r2dbc         | **1.0.0.M1** | [link](https://repo.spring.io/milestone/org/springframework/data/spring-data-r2dbc/1.0.0.M1/)
| 1.0.0.M7 :     |                           |              | [release log](https://r2dbc.io/2019/02/09/r2dbc-1-0-milestone-7-released)
| 2019-02-09     | io.r2dbc:r2dbc-spi        | 1.0.0.M7     | ↑
|                | io.r2dbc:r2dbc-client     | 1.0.0.M7     | ↑
|                | io.r2dbc:r2dbc-postgresql | 1.0.0.M7     | ↑
|                | io.r2dbc:r2dbc-h2         | 1.0.0.M7     | ↑
|                | io.r2dbc:r2dbc-mssql      | 1.0.0.M7     | ↑
|                | io.r2dbc:r2dbc-bom        | 1.0.0.M7     | + new
|                | io.r2dbc:r2dbc-proxy      | 1.0.0.M7     | + new
|                | Specification             | 1.0.0.M7     | + new
| 1.0.0.M6 :     |                           |              | [release log](https://r2dbc.io/2018/11/19/r2dbc-1-0-milestone-6-released)
| 2018-11-19     | io.r2dbc:r2dbc-spi        | 1.0.0.M6     | ↑
|                | io.r2dbc:r2dbc-client     | 1.0.0.M6     | ↑
|                | io.r2dbc:r2dbc-postgresql | 1.0.0.M6     | ↑
|                | io.r2dbc:r2dbc-h2         | 1.0.0.M6     | + new driver
|                | io.r2dbc:r2dbc-mssql      | 1.0.0.M6     | + new driver
| 1.0.0.M5 :
| **2018-09-24** | io.r2dbc:r2dbc-spi        | **1.0.0.M5** | ↑
|                | io.r2dbc:r2dbc-client     | 1.0.0.M5     | ↑
|                | io.r2dbc:r2dbc-postgresql | 1.0.0.M5     | ↑

## 4. Ref

- 2018-12-19 spring blog : [Spring Tips: Reactive SQL Data Access with Spring Data R2DBC](https://spring.io/blog/2018/12/19/spring-tips-reactive-sql-data-access-with-spring-data-r2dbc) - youtube video
- 2018-12-12 spring blog : [Spring Data R2DBC 1.0 M1 released](https://spring.io/blog/2018/12/12/spring-data-r2dbc-1-0-m1-released)
- 2018-10-01 infoQ : [Experimental Reactive Relational Database Connectivity Driver, R2DBC, Announced at SpringOne](https://www.infoq.com/news/2018/10/springone-r2dbc)
- 2018-09-27 spring blog : [The Reactive Revolution at SpringOne Platform 2018 (part 1/N)](https://spring.io/blog/2018/09/27/the-reactive-revolution-at-springone-platform-2018-part-1-n)
- spring-guides : [Accessing Relational Data using JDBC with Spring](https://spring.io/guides/gs/relational-data-access/)


[R2DBC]: https://r2dbc.io
[spring-data-r2dbc]: https://github.com/spring-projects/spring-data-r2dbc