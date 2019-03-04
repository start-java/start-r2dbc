package tech.simter.start.r2dbc.spi.bindparam;

import io.r2dbc.spi.Result;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import tech.simter.start.r2dbc.spi.AbstractSpiTest;

/**
 * @author RJ
 */
class BindStringToSqlVarcharTest extends AbstractSpiTest {
  @Override
  protected String getCreateTableSql() {
    return "create table t(id varchar(255))";
  }

  private String MAX_LEN_STRING = "";

  @BeforeAll
  @Override
  protected void setup() {
    super.setup();
    StringBuilder t = new StringBuilder();
    for (int i = 0; i < 255; i++) t.append('0');
    MAX_LEN_STRING = t.toString();
  }

  @Test
  void test() {
    // insert into
    String id = MAX_LEN_STRING;
    connection()
      .flatMapMany(c -> c.createStatement("insert into t(id) values($1)")
        .bind("$1", id)
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1)
      .verifyComplete();

    // select it
    connection()
      .flatMapMany(c -> c.createStatement("select id from t where id = $1")
        .bind("$1", id)
        .execute())
      .flatMap(result -> result.map((row, metadata) -> row.get("id", String.class)))
      .as(StepVerifier::create)
      .expectNext(id)
      .verifyComplete();

    // select nothing
    connection()
      .flatMapMany(c -> c.createStatement("select id from t where id = $1")
        .bind("$1", id.substring(1))
        .execute())
      .flatMap(result -> result.map((row, metadata) -> row.get("id", Integer.class)))
      .as(StepVerifier::create)
      .verifyComplete();

    // update it
    connection()
      .flatMapMany(c -> c.createStatement("update t set id = $1 where id = $1")
        .bind("$1", id)
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1)
      .verifyComplete();
  }
}