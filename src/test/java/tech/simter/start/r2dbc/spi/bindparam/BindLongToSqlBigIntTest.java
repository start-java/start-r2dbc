package tech.simter.start.r2dbc.spi.bindparam;

import io.r2dbc.spi.Result;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import tech.simter.start.r2dbc.spi.AbstractSpiTest;

/**
 * {@code Long} 's value is between -9223372036854775808L=-2<sup>63</sup> and 9223372036854775807L=2<sup>63</sup>-1.
 *
 * @author RJ
 */
class BindLongToSqlBigIntTest extends AbstractSpiTest {
  @Override
  protected String getCreateTableSql() {
    return "create table t(id bigint)";
  }

  @Test
  void test() {
    // insert into
    connection()
      .flatMapMany(c -> c.createStatement("insert into t(id) values($1)")
        .bind("$1", Long.MAX_VALUE)
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1)
      .verifyComplete();

    // select max value
    connection()
      .flatMapMany(c -> c.createStatement("select id from t where id = $1")
        .bind("$1", Long.MAX_VALUE)
        .execute())
      .flatMap(result -> result.map((row, metadata) -> row.get("id", Long.class)))
      .as(StepVerifier::create)
      .expectNext(Long.MAX_VALUE)
      .verifyComplete();

    // update to min value
    connection()
      .flatMapMany(c -> c.createStatement("update t set id = $2 where id = $1")
        .bind("$1", Long.MAX_VALUE)
        .bind("$2", Long.MIN_VALUE)
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1)
      .verifyComplete();

    // select min value
    connection()
      .flatMapMany(c -> c.createStatement("select id from t where id = $1")
        .bind("$1", Long.MIN_VALUE)
        .execute())
      .flatMap(result -> result.map((row, metadata) -> row.get("id", Long.class)))
      .as(StepVerifier::create)
      .expectNext(Long.MIN_VALUE)
      .verifyComplete();
  }
}