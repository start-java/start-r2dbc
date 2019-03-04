package tech.simter.start.r2dbc.spi.bindparam;

import io.r2dbc.spi.Result;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import tech.simter.start.r2dbc.spi.AbstractSpiTest;

/**
 * {@code Short} 's value is between -32768=-2<sup>15</sup> and 32767=2<sup>15</sup>-1.
 *
 * @author RJ
 */
class BindShortToSqlSmallIntTest extends AbstractSpiTest {
  @Override
  protected String getCreateTableSql() {
    return "create table t(id smallint)";
  }

  @Test
  void test() {
    // insert into
    connection()
      .flatMapMany(c -> c.createStatement("insert into t(id) values($1)")
        .bind("$1", Short.MAX_VALUE)
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1)
      .verifyComplete();

    // select max value
    connection()
      .flatMapMany(c -> c.createStatement("select id from t where id = $1")
        .bind("$1", Short.MAX_VALUE)
        .execute())
      .flatMap(result -> result.map((row, metadata) -> row.get("id", Short.class)))
      .as(StepVerifier::create)
      .expectNext(Short.MAX_VALUE)
      .verifyComplete();

    // update to min value
    connection()
      .flatMapMany(c -> c.createStatement("update t set id = $2 where id = $1")
        .bind("$1", Short.MAX_VALUE)
        .bind("$2", Short.MIN_VALUE)
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1)
      .verifyComplete();

    // select min value
    connection()
      .flatMapMany(c -> c.createStatement("select id from t where id = $1")
        .bind("$1", Short.MIN_VALUE)
        .execute())
      .flatMap(result -> result.map((row, metadata) -> row.get("id", Short.class)))
      .as(StepVerifier::create)
      .expectNext(Short.MIN_VALUE)
      .verifyComplete();
  }
}