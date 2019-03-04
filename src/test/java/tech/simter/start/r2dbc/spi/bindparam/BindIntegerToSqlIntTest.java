package tech.simter.start.r2dbc.spi.bindparam;

import io.r2dbc.spi.Result;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import tech.simter.start.r2dbc.spi.AbstractSpiTest;

/**
 * {@code Integer} 's value is between -2147483648=-2<sup>31</sup> and 2147483647=2<sup>31</sup>-1.
 *
 * @author RJ
 */
class BindIntegerToSqlIntTest extends AbstractSpiTest {
  @Override
  protected String getCreateTableSql() {
    return "create table t(id int)";
  }

  @Test
  void test() {
    // insert into
    connection()
      .flatMapMany(c -> c.createStatement("insert into t(id) values($1)")
        .bind("$1", Integer.MAX_VALUE)
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1)
      .verifyComplete();

    // select max value
    connection()
      .flatMapMany(c -> c.createStatement("select id from t where id = $1")
        .bind("$1", Integer.MAX_VALUE)
        .execute())
      .flatMap(result -> result.map((row, metadata) -> row.get("id", Integer.class)))
      .as(StepVerifier::create)
      .expectNext(Integer.MAX_VALUE)
      .verifyComplete();

    // update to min value
    connection()
      .flatMapMany(c -> c.createStatement("update t set id = $2 where id = $1")
        .bind("$1", Integer.MAX_VALUE)
        .bind("$2", Integer.MIN_VALUE)
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1)
      .verifyComplete();

    // select min value
    connection()
      .flatMapMany(c -> c.createStatement("select id from t where id = $1")
        .bind("$1", Integer.MIN_VALUE)
        .execute())
      .flatMap(result -> result.map((row, metadata) -> row.get("id", Integer.class)))
      .as(StepVerifier::create)
      .expectNext(Integer.MIN_VALUE)
      .verifyComplete();
  }
}