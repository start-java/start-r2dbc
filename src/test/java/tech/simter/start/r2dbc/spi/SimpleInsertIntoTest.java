package tech.simter.start.r2dbc.spi;

import io.r2dbc.spi.Result;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static tech.simter.start.r2dbc.TestUtils.nextId;

/**
 * @author RJ
 */
class SimpleInsertIntoTest extends AbstractSpiTest {
  /**
   * with r2dbc-1.0.0.M6 :
   * 1. postgres : success
   * 2. h2: failed (expected: onNext(1); actual: onError(org.h2.message.DbException: Method is only allowed for a query. Use execute or executeUpdate instead of executeQuery [90002-197]))
   */
  @Test
  void insertBySelect() {
    // insert test data
    int id1 = nextId();
    connection()
      .flatMapMany(c ->
        c.createStatement("insert into t(id) values($1)")
          .bind("$1", id1)
          .execute()
      )
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1).verifyComplete();

    // insert by select
    int id2 = nextId();
    String sql = "insert into t(id) select $2 from T where id = $1";
    connection()
      .flatMapMany(c -> c.createStatement(sql)
        .bind("$1", id1)
        .bind("$2", id2)
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1).verifyComplete();
  }

  @Test
  void insertOneAndGetUpdatedCount() {
    int id = nextId();
    String sql = "insert into t(id) values($1)";

    connection()
      .flatMapMany(c ->
        c.createStatement(sql)
          .bind("$1", id)
          .execute()
      ).flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1).verifyComplete();
  }

  @Test
  void insertOneAndDoResultMapWithoutBind() {
    connection()
      .flatMapMany(c -> c.createStatement("insert into t(id) values(9)").execute())
      .flatMap(result -> result.map((row, rowMetadata) -> 1))
      .as(StepVerifier::create)
      .verifyComplete();
  }

  /**
   * with r2dbc-1.0.0.M6/M7 :
   * 1. postgres : run forever
   * 2. h2: not invoked
   */
  @Test
  @Disabled("postgres will run forever")
  void insertOneAndDoResultMapWithBind() {
    connection()
      .flatMapMany(c -> c.createStatement("insert into t(id) values($1)").bind("$1", 9).execute())
      .flatMap(result -> result.map((row, rowMetadata) -> 1))
      .as(StepVerifier::create)
      .verifyComplete();
  }

  @Test
  void insertFailedByUniqueConstrain() {
    // insert test data
    int id = nextId();
    String sql = "insert into t(id) values($1)";
    connection()
      .flatMapMany(c ->
        c.createStatement(sql)
          .bind("$1", id)
          .execute()
      )
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1).verifyComplete();

    // insert it again
    connection()
      .flatMapMany(c ->
        c.createStatement(sql)
          .bind("$1", id)
          .execute()
      )
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .consumeErrorWith(e -> {
        // logger.error("insertOne error", e);
        String clazz = e.getClass().getName();
        if (clazz.contains(".h2.")) {
          assertEquals("org.h2.message.DbException", clazz);
          assertEquals("Unique index or primary key violation: \"PRIMARY KEY ON PUBLIC.t(id)\" [23505-197]", e.getMessage());
        } else if (clazz.contains(".postgresql.")) {
          assertEquals("io.r2dbc.postgresql.PostgresqlServerErrorException", clazz);
          // e.etMessage():
          // zh_cn 重复键违反唯一约束"t_pkey"
          // eng   duplicate key value violates unique constraint "t_pkey"
        } else {
          throw new RuntimeException("TODO : other database");
        }
      }).verify();
  }

  @Test
  void insertRowsBySingleInsertInto() {
    int[] ids = new int[]{nextId(), nextId()};
    String sql = "insert into t(id) values($1), ($2)";
    connection()
      .flatMapMany(c -> c.createStatement(sql)
        .bind("$1", ids[0])
        .bind("$2", ids[1])
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(2).verifyComplete();
  }

  @Test
  void insertRowsByMultipleInsertIntoWithoutBind() {
    int[] ids = new int[]{nextId(), nextId(), nextId()};
    String sql = "insert into t(id) values(" + ids[0] + ");\ninsert into t(id) values(" + ids[1] + "), (" + ids[2] + ");";
    connection()
      .flatMapMany(c -> c.createStatement(sql).execute())
      .flatMap(Result::getRowsUpdated) // each 'insert into' return one result
      .as(StepVerifier::create)
      .expectNext(1).expectNext(2).verifyComplete();
  }

  /**
   * with r2dbc-1.0.0.M7 :
   * 1. postgres : failed (expected: onNext(1); actual: onError(java.lang.IllegalArgumentException: Statement 'insert into t(id) values($1);
   * insert into t(id) values($2), ($3);' cannot be created. This is often due to the presence of both multiple statements and parameters at the same time.))
   * 2. h2: failed (expected: onNext(1); actual: onError(java.lang.ArrayIndexOutOfBoundsException: 3))
   * <p>
   * with r2dbc-1.0.0.M6 :
   * 1. postgres : failed (expected: onNext(1); actual: onError(java.lang.UnsupportedOperationException: Binding parameters is not supported for the statement 'insert into t(id) values($1);
   * insert into t(id) values($2), ($3);'))
   * 2. h2: failed (expected: onNext(1); actual: onError(java.lang.ArrayIndexOutOfBoundsException: 3))
   */
  @Test
  void insertRowsByMultipleInsertIntoWithBind() {
    int[] ids = new int[]{nextId(), nextId(), nextId()};
    String sql = "insert into t(id) values($1);\ninsert into t(id) values($2), ($3);";
    connection()
      .flatMapMany(c -> c.createStatement(sql)
        .bind("$1", ids[0])
        .bind("$2", ids[1])
        .bind("$3", ids[2])
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1).expectNext(2).verifyComplete();
  }
}