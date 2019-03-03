package tech.simter.start.r2dbc.spi;

import io.r2dbc.spi.Result;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static tech.simter.start.r2dbc.TestUtils.nextId;

/**
 * @author RJ
 */
class SimpleSelectTest extends AbstractSpiTest {
  @Test
  void selectBySingleLineSql() {
    // insert test data
    int[] ids = new int[]{nextId(), nextId()};
    connection()
      .flatMapMany(c -> c.createStatement("insert into t(id) values($1), ($2)")
        .bind("$1", ids[0])
        .bind("$2", ids[1])
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(2).verifyComplete();

    // select them
    String sql = "select id from T where id in ($1, $2) order by id asc";
    connection()
      .flatMapMany(c -> c.createStatement(sql)
        .bind("$1", ids[0])
        .bind("$2", ids[1])
        .execute())
      .flatMap(result -> result.map((row, metadata) -> row.get("id", Integer.class)))
      .as(StepVerifier::create)
      .expectNext(ids[0]).expectNext(ids[1]).verifyComplete();
  }

  /**
   * r2dbc-1.0.0.M7 fixed.
   * <p>
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
    connection()
      .flatMapMany(c -> c.createStatement("insert into t(id) values($1), ($2)")
        .bind("$1", ids[0])
        .bind("$2", ids[1])
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(2).verifyComplete();

    // select them
    String sql = "select id from T" +
      "\n  where id in ($1, $2)" +
      "\n  order by id asc";
    connection()
      .flatMapMany(c -> c.createStatement(sql)
        .bind("$1", ids[0])
        .bind("$2", ids[1])
        .execute())
      .flatMap(result -> result.map((row, metadata) -> row.get("id", Integer.class)))
      .as(StepVerifier::create)
      .expectNext(ids[0]).expectNext(ids[1]).verifyComplete();
  }

  @Test
  void selectWithAlias() {
    // insert test data
    int[] ids = new int[]{nextId(), nextId()};
    connection()
      .flatMapMany(c -> c.createStatement("insert into t(id) values($1), ($2)")
        .bind("$1", ids[0])
        .bind("$2", ids[1])
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(2).verifyComplete();

    // select them
    String sql = "select id as my_id from T where id in ($1, $2) order by id asc";
    connection()
      .flatMapMany(c -> c.createStatement(sql)
        .bind("$1", ids[0])
        .bind("$2", ids[1])
        .execute())
      .flatMap(result -> result.map((row, metadata) -> row.get("my_id", Integer.class)))
      .as(StepVerifier::create)
      .expectNext(ids[0]).expectNext(ids[1]).verifyComplete();
  }
}