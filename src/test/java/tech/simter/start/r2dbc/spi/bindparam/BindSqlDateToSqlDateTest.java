package tech.simter.start.r2dbc.spi.bindparam;

import io.r2dbc.spi.Result;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import tech.simter.start.r2dbc.spi.AbstractSpiTest;

import java.sql.Date;
import java.time.LocalDate;
import java.util.Optional;

import static tech.simter.start.r2dbc.TestUtils.nextId;

/**
 * @author RJ
 */
class BindSqlDateToSqlDateTest extends AbstractSpiTest {
  @Override
  protected String getCreateTableSql() {
    return "create table t(id int primary key, value date)";
  }

  @Test
  void notNullValue() {
    // insert into
    Integer id = nextId();
    Date value = Date.valueOf(LocalDate.now());
    connection()
      .flatMapMany(c -> c.createStatement("insert into t(id, value) values($1, $2)")
        .bind("$1", id)
        .bind("$2", value)
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1)
      .verifyComplete();

    // select
    connection()
      .flatMapMany(c -> c.createStatement("select value from t where id = $1")
        .bind("$1", id)
        .execute())
      .flatMap(result -> result.map((row, metadata) -> row.get("value", Date.class)))
      .as(StepVerifier::create)
      .expectNext(value)
      .verifyComplete();

    // update
    Date newValue = Date.valueOf(LocalDate.now().minusDays(1));
    connection()
      .flatMapMany(c -> c.createStatement("update t set value = $2 where id = $1")
        .bind("$1", id)
        .bind("$2", newValue)
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1)
      .verifyComplete();
  }

  @Test
  void nullValue() {
    // insert into
    Integer id = nextId();
    connection()
      .flatMapMany(c -> c.createStatement("insert into t(id, value) values($1, $2)")
        .bind("$1", id)
        .bindNull("$2", Date.class)
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1)
      .verifyComplete();

    // select
    connection()
      .flatMapMany(c -> c.createStatement("select value from t where id = $1")
        .bind("$1", id)
        .execute())
      .flatMap(result -> result.map((row, metadata) -> Optional.ofNullable(row.get("value", Date.class))))
      .as(StepVerifier::create)
      .expectNext(Optional.ofNullable(null))
      .verifyComplete();

    // update
    connection()
      .flatMapMany(c -> c.createStatement("update t set value = $2 where id = $1")
        .bind("$1", id)
        .bindNull("$2", Date.class)
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1)
      .verifyComplete();
  }
}