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

  private boolean cache = true;

  // build connection
  private Mono<Connection> connection(boolean cache) {
    if (cache) {                 // cache
      if (cacheConnection == null) cacheConnection = Mono.from(connectionFactory.create());
      return cacheConnection;
    } else {                     // new
      logger.warn("create a new connection");
      return Mono.from(connectionFactory.create());
    }
  }

  // a cache connection
  private Mono<Connection> cacheConnection;

  private int id = 100;

  private int nextId() {
    return ++id;
  }

  @BeforeAll
  void setup() {
    logger.warn("cache={}, connection will {}be reused for all test method.", cache, cache ? "" : "not ");
    // create test table
    String sql = "create table T(id int primary key);";
    StepVerifier.create(
      connection(cache)
        .flatMapMany(c -> c.createStatement(sql).execute())
        //.flatMap(Result::getRowsUpdated) // TODO : h2 return 0, postgres return empty
        .then()
    ).verifyComplete();
  }

  @AfterAll
  void release() {
    // drop test table
    String sql = "drop table if exists T;";
    StepVerifier.create(
      connection(cache)
        .flatMapMany(c -> c.createStatement(sql).execute())
        //.flatMap(Result::getRowsUpdated) // TODO : h2 return 0, postgres return empty
        .then()
    ).verifyComplete();

    // test drop error
    String sql1 = "drop table T;";
    StepVerifier.create(
      connection(cache)
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

    // close connection
    if (cacheConnection != null)
      StepVerifier.create(cacheConnection.flatMapMany(Connection::close))
        .verifyComplete();
  }

  @Test
  void deleteAll() {
    StepVerifier.create(
      connection(cache)
        .flatMapMany(c -> c.createStatement("delete from T").execute())
        .then()
    ).verifyComplete();
  }

  /**
   * with r2dbc-1.0.0.M6 :
   * 1. postgres : success
   * 2. h2: failed (expected: onNext(1); actual: onError(org.h2.message.DbException: Method is only allowed for a query. Use execute or executeUpdate instead of executeQuery [90002-197]))
   */
  @Test
  void insertBySelect() {
    // insert test data
    int id1 = nextId();
    StepVerifier.create(
      connection(cache)
        .flatMapMany(c ->
          c.createStatement("insert into T(id) values($1)")
            .bind("$1", id1)
            .execute()
        )
        .flatMap(Result::getRowsUpdated)
    ).expectNext(1).verifyComplete();

    // insert by select
    int id2 = nextId();
    String sql = "insert into T(id) select $2 from T where id = $1";
    StepVerifier.create(
      connection(cache)
        .flatMapMany(c -> c.createStatement(sql)
          .bind("$1", id1)
          .bind("$2", id2)
          .execute())
        .flatMap(Result::getRowsUpdated)
    ).expectNext(1).verifyComplete();
  }

  @Test
  void insertOneAndGetUpdatedCount() {
    int id = nextId();
    String sql = "insert into T(id) values($1)";

    StepVerifier.create(
      connection(cache)
        .flatMapMany(c ->
          c.createStatement(sql)
            .bind("$1", id)
            .execute()
        ).flatMap(Result::getRowsUpdated)
    ).expectNext(1).verifyComplete();
  }

  @Test
  void insertOneAndDoResultMapWithoutBind() {
    StepVerifier.create(
      connection(cache)
        .flatMapMany(c -> c.createStatement("insert into T(id) values(9)").execute())
        .flatMap(result -> result.map((row, rowMetadata) -> 1))
    ).verifyComplete();
  }

  /**
   * with r2dbc-1.0.0.M6/M7 :
   * 1. postgres : run forever
   * 2. h2: not invoked
   */
  @Test
  @Disabled("postgres will run forever")
  void insertOneAndDoResultMapWithBind() {
    StepVerifier.create(
      connection(cache)
        .flatMapMany(c -> c.createStatement("insert into T(id) values($1)").bind("$1", 9).execute())
        .flatMap(result -> result.map((row, rowMetadata) -> 1))
    ).verifyComplete();
  }

  @Test
  void insertFailedByUniqueConstrain() {
    // insert test data
    int id = nextId();
    String sql = "insert into T(id) values($1)";
    StepVerifier.create(
      connection(cache)
        .flatMapMany(c ->
          c.createStatement(sql)
            .bind("$1", id)
            .execute()
        )
        .flatMap(Result::getRowsUpdated)
    ).expectNext(1).verifyComplete();

    // insert it again
    StepVerifier.create(
      connection(cache)
        .flatMapMany(c ->
          c.createStatement(sql)
            .bind("$1", id)
            .execute()
        )
        .flatMap(Result::getRowsUpdated)
    ).consumeErrorWith(e -> {
      // logger.error("insertOne error", e);
      String clazz = e.getClass().getName();
      if (clazz.contains(".h2.")) {
        assertEquals("org.h2.message.DbException", clazz);
        assertEquals("Unique index or primary key violation: \"PRIMARY KEY ON PUBLIC.T(ID)\" [23505-197]", e.getMessage());
      } else if (clazz.contains(".postgresql.")) {
        assertEquals("io.r2dbc.postgresql.PostgresqlServerErrorException", clazz);
        assertEquals("重复键违反唯一约束\"t_pkey\"", e.getMessage());
      } else {
        throw new RuntimeException("TODO : other database");
      }
    }).verify();
  }

  @Test
  void insertRowsBySingleInsertInto() {
    int[] ids = new int[]{nextId(), nextId()};
    String sql = "insert into T(id) values($1), ($2)";
    StepVerifier.create(
      connection(cache)
        .flatMapMany(c -> c.createStatement(sql)
          .bind("$1", ids[0])
          .bind("$2", ids[1])
          .execute())
        .flatMap(Result::getRowsUpdated)
    ).expectNext(2).verifyComplete();
  }

  @Test
  void insertRowsByMultipleInsertIntoWithoutBind() {
    int[] ids = new int[]{nextId(), nextId(), nextId()};
    String sql = "insert into T(id) values(" + ids[0] + ");\ninsert into T(id) values(" + ids[1] + "), (" + ids[2] + ");";
    StepVerifier.create(
      connection(cache)
        .flatMapMany(c -> c.createStatement(sql).execute())
        .flatMap(Result::getRowsUpdated) // each 'insert into' return one result
    ).expectNext(1).expectNext(2).verifyComplete();
  }

  /**
   * with r2dbc-1.0.0.M6 :
   * 1. postgres : failed (expected: onNext(1); actual: onError(java.lang.UnsupportedOperationException: Binding parameters is not supported for the statement 'insert into T(id) values($1);
   * insert into T(id) values($2), ($3);'))
   * 2. h2: failed (expected: onNext(1); actual: onError(java.lang.ArrayIndexOutOfBoundsException: 3))
   */
  @Test
  void insertRowsByMultipleInsertIntoWithBind() {
    int[] ids = new int[]{nextId(), nextId(), nextId()};
    String sql = "insert into T(id) values($1);\ninsert into T(id) values($2), ($3);";
    StepVerifier.create(
      connection(cache)
        .flatMapMany(c -> c.createStatement(sql)
          .bind("$1", ids[0])
          .bind("$2", ids[1])
          .bind("$3", ids[3])
          .execute())
        .flatMap(Result::getRowsUpdated)
    ).expectNext(1).expectNext(2).verifyComplete();
  }

  @Test
  void selectBySingleLineSql() {
    // insert test data
    int[] ids = new int[]{nextId(), nextId()};
    StepVerifier.create(
      connection(cache)
        .flatMapMany(c -> c.createStatement("insert into T(id) values($1), ($2)")
          .bind("$1", ids[0])
          .bind("$2", ids[1])
          .execute())
        .flatMap(Result::getRowsUpdated)
    ).expectNext(2).verifyComplete();

    // select them
    String sql = "select id from T where id in ($1, $2) order by id asc";
    StepVerifier.create(
      connection(cache)
        .flatMapMany(c -> c.createStatement(sql)
          .bind("$1", ids[0])
          .bind("$2", ids[1])
          .execute())
        .flatMap(result -> result.map((row, metadata) -> row.get("id", Integer.class)))
    ).expectNext(ids[0]).expectNext(ids[1]).verifyComplete();
  }

  /**
   * with r2dbc-1.0.0.M6 :
   * 1. postgres : failed (expected: onNext(101); actual: onError(java.lang.UnsupportedOperationException: Binding parameters is not supported for the statement 'select id from T
   * where id in ($1, $2)
   * order by id asc'))
   * 2. h2: expected: onNext(101); actual: onError(org.h2.message.DbException: Method is not allowed for a query. Use execute or executeQuery instead of executeUpdate [90001-197]))
   */
  @Test
  void selectByMultiLineSql() {
    // insert test data
    int[] ids = new int[]{nextId(), nextId()};
    StepVerifier.create(
      connection(cache)
        .flatMapMany(c -> c.createStatement("insert into T(id) values($1), ($2)")
          .bind("$1", ids[0])
          .bind("$2", ids[1])
          .execute())
        .flatMap(Result::getRowsUpdated)
    ).expectNext(2).verifyComplete();

    // select them
    String sql = "select id from T" +
      "\n  where id in ($1, $2)" +
      "\n  order by id asc";
    StepVerifier.create(
      connection(cache)
        .flatMapMany(c -> c.createStatement(sql)
          .bind("$1", ids[0])
          .bind("$2", ids[1])
          .execute())
        .flatMap(result -> result.map((row, metadata) -> row.get("id", Integer.class)))
    ).expectNext(ids[0]).expectNext(ids[1]).verifyComplete();
  }
}