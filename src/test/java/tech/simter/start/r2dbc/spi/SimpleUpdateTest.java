package tech.simter.start.r2dbc.spi;

import io.r2dbc.spi.Result;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static tech.simter.start.r2dbc.TestUtils.nextId;

/**
 * @author RJ
 */
class SimpleUpdateTest extends AbstractSpiTest {
  @Test
  void update() {
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
      .expectNext(1)
      .verifyComplete();

    // update it
    int id2 = nextId();
    connection()
      .flatMapMany(c -> c.createStatement("update t set id = $2 where id = $1")
        .bind("$1", id1)
        .bind("$2", id2)
        .execute())
      .flatMap(Result::getRowsUpdated)
      .as(StepVerifier::create)
      .expectNext(1)
      .verifyComplete();

    // verify id1 not exists
    connection()
      .flatMapMany(c -> c.createStatement("select id from t where id = $1")
        .bind("$1", id1)
        .execute())
      .flatMap(result -> result.map((row, rowMetadata) -> row.get("id", Integer.class)))
      .as(StepVerifier::create)
      .verifyComplete();

    // verify id2 not exists
    connection()
      .flatMapMany(c -> c.createStatement("select id from t where id = $1")
        .bind("$1", id2)
        .execute())
      .flatMap(result -> result.map((row, rowMetadata) -> row.get("id", Integer.class)))
      .as(StepVerifier::create)
      .expectNext(id2)
      .verifyComplete();
  }
}