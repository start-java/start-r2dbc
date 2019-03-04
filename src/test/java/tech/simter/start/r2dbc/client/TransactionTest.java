package tech.simter.start.r2dbc.client;

import io.r2dbc.client.R2dbc;
import io.r2dbc.spi.Result;
import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;
import tech.simter.start.r2dbc.spi.AbstractSpiTest;

import static tech.simter.start.r2dbc.TestUtils.nextId;

/**
 * See <a href="https://github.com/r2dbc/r2dbc-client/blob/master/src/test/java/io/r2dbc/client/R2dbcTest.java">r2dbc-client/.../R2dbcTest.java</a>
 *
 * @author RJ
 */
class TransactionTest extends AbstractSpiTest {
  @Test
  void singleInsertAndGetUpdatedCount() {
    Integer id = nextId();
    new R2dbc(connectionFactory)
      .inTransaction(handle ->
        handle.execute("insert into t(id) values($1)", id))
      .as(StepVerifier::create)
      .expectNext(1)
      .verifyComplete();
  }

  @Test
  void batchInsertAndGetUpdatedCount() {
    Integer[] ids = new Integer[]{nextId(), nextId()};
    new R2dbc(connectionFactory)
      .inTransaction(handle -> handle
        .createBatch()
        // TODO Question: how to bind params ?
        .add("insert into t(id) values(" + ids[0] + ")")
        .add("insert into t(id) values(" + ids[1] + ")")
        .mapResult(Result::getRowsUpdated))
      .as(StepVerifier::create)
      .expectNext(1)
      .expectNext(1)
      .verifyComplete();
  }
}