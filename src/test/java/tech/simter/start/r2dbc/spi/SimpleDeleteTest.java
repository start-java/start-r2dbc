package tech.simter.start.r2dbc.spi;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

/**
 * @author RJ
 */
class SimpleDeleteTest extends AbstractSpiTest {
  @Test
  void deleteAll() {
    connection()
      .flatMapMany(c -> c.createStatement("delete from t").execute())
      .then()
      .as(StepVerifier::create)
      .verifyComplete();
  }
}