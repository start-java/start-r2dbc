package tech.simter.start.r2dbc;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import io.r2dbc.spi.Result;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

/**
 * @author RJ
 */
@SpringBootTest(classes = UnitTestConfiguration.class)
@ExtendWith(SpringExtension.class)
@TestInstance(PER_CLASS)
class ConnectionFactoryTest {
  private Logger logger = LoggerFactory.getLogger(ConnectionFactoryTest.class);
  @Autowired
  private ConnectionFactory connectionFactory;

  private Mono<Connection> connection() {
    return Mono.from(connectionFactory.create());
  }

  @BeforeAll
  void createTable() {
    String sql = "create table T(id int primary key);";
    StepVerifier.create(
      connection()
        .flatMapMany(c -> c.createStatement(sql).execute())
        .flatMap(Result::getRowsUpdated)
    ).expectNext(0).verifyComplete();
  }

  @AfterAll
  void dropTable() {
    // success
    String sql = "drop table if exists T;";
    StepVerifier.create(
      connection()
        .flatMapMany(c -> c.createStatement(sql).execute())
        .flatMap(Result::getRowsUpdated)
    ).expectNext(0).verifyComplete();

    // error because table not exists
    String sql1 = "drop table T;";
    StepVerifier.create(
      connection()
        .flatMapMany(c -> c.createStatement(sql1).execute())
        .flatMap(Result::getRowsUpdated)
    ).consumeErrorWith(e -> {
      // logger.error("drop error", e);
      String clazz = e.getClass().getName();
      if (clazz.startsWith("org.h2")) {
        assertEquals("org.h2.message.DbException", clazz);
        assertEquals("Table \"T\" not found [42102-197]", e.getMessage());
      } else if (clazz.startsWith("org.postgresql")) {
        assertEquals("org.postgresql.?", clazz);
        assertEquals("?", e.getMessage());
      }
    }).verify();
  }

  @Test
  void deleteAll() {
    String sql = "delete from T";

    // success
    StepVerifier.create(
      connection()
        .flatMapMany(c -> c.createStatement(sql).execute())
        .then()
    ).verifyComplete();
  }

  @Test
  @Disabled("test failed in h2: org.h2.message.DbException: Method is only allowed for a query. Use execute or executeUpdate instead of executeQuery [90002-197])")
  void insertNothing() {
    String sql = "insert into T(id) select id from T where id is null";

    // success and get effected row count
    StepVerifier.create(
      connection()
        .flatMapMany(c -> c.createStatement(sql).execute())
        .flatMap(Result::getRowsUpdated)
    ).expectNext(0).verifyComplete();
  }

  @Test
  void insertOne() {
    String sql = "insert into T(id) values($1)";

    // success and get effected row count
    StepVerifier.create(
      connection()
        .flatMapMany(c ->
          c.createStatement(sql)
            .bind("$1", 1)
            .execute()
        )
        .flatMap(Result::getRowsUpdated)
    ).expectNext(1).verifyComplete();

    // success and get result but no result
    StepVerifier.create(
      connection()
        .flatMapMany(c ->
          c.createStatement(sql)
            .bind("$1", 2)
            .execute()
        )
        .flatMap(result -> result.map((row, rowMetadata) -> { // not invoked because no rows
          logger.debug("result columns :");
          rowMetadata.getColumnMetadatas().forEach(c ->
            logger.debug("  name={}, type={}, precision={}", c.getName(), c.getType(), c.getPrecision().orElse(-1))
          );
          return 1;
        }))
    ).verifyComplete();

    // error because primary key unique constrain
    StepVerifier.create(
      connection()
        .flatMapMany(c ->
          c.createStatement(sql)
            .bind("$1", 1)
            .execute()
        )
        .flatMap(Result::getRowsUpdated)
    ).consumeErrorWith(e -> {
      // logger.error("insertOne error", e);
      String clazz = e.getClass().getName();
      if (clazz.startsWith("org.h2")) {
        assertEquals("org.h2.message.DbException", clazz);
        assertEquals("Unique index or primary key violation: \"PRIMARY KEY ON PUBLIC.T(ID)\" [23505-197]", e.getMessage());
      } else if (clazz.startsWith("org.postgresql")) {
        assertEquals("org.postgresql.?", clazz);
        assertEquals("?", e.getMessage());
      }
    }).verify();
  }

  @Test
  void insertTwo() {
    // multiple insert into
    String sql = "insert into T(id) values(10);insert into T(id) values(11), (12);";
    StepVerifier.create(
      connection()
        .flatMapMany(c -> c.createStatement(sql).execute())
        .flatMap(Result::getRowsUpdated) // each 'insert into' return one result
    ).expectNext(1).expectNext(2).verifyComplete();

    // single insert into
    String sql1 = "insert into T(id) values(15), (16);";
    StepVerifier.create(
      connection()
        .flatMapMany(c -> c.createStatement(sql1).execute())
        .flatMap(Result::getRowsUpdated)
    ).expectNext(2).verifyComplete();
  }
}