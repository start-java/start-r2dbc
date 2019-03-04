package tech.simter.start.r2dbc.spi;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import tech.simter.start.r2dbc.ConnectionFactoryConfiguration;

import static org.junit.jupiter.api.TestInstance.Lifecycle.PER_CLASS;

/**
 * @author RJ
 */
@SpringBootTest(classes = ConnectionFactoryConfiguration.class)
@ExtendWith(SpringExtension.class)
@TestInstance(PER_CLASS)
public class AbstractSpiTest {
  private Logger logger = LoggerFactory.getLogger(AbstractSpiTest.class);
  @Autowired
  protected ConnectionFactory connectionFactory;

  protected boolean isCache() {
    return true;
  }

  protected Mono<Connection> connection() {
    return connection(isCache());
  }

  // build connection
  private Mono<Connection> connection(boolean cache) {
    if (cache) {                 // cache
      if (cacheConnection == null) cacheConnection = Mono.from(connectionFactory.create());
      return cacheConnection;
    } else {                     // new
      logger.warn("create a new connection");
      return Mono.from(connectionFactory.create());
    }
  }

  // a cache connection
  private Mono<Connection> cacheConnection;

  protected String getCreateTableSql() {
    return "create table t(id int)";
  }

  @BeforeAll
  protected void setup() {
    logger.warn("Connection will {}be reused for all test method.", isCache() ? "" : "not ");
    // create test table
    StepVerifier.create(
      connection()
        .flatMapMany(c -> c.createStatement(getCreateTableSql()).execute())
        //.flatMap(Result::getRowsUpdated) // TODO : h2 return 0, postgres return empty
        .then()
    ).verifyComplete();
  }

  private String getDropTableSql() {
    return "drop table if exists t";
  }

  @AfterAll
  protected void release() {
    // drop test table
    StepVerifier.create(
      connection()
        .flatMapMany(c -> c.createStatement(getDropTableSql()).execute())
        //.flatMap(Result::getRowsUpdated) // TODO : h2 return 0, postgres return empty
        .then()
    ).verifyComplete();

    // close connection
    if (cacheConnection != null) {
      StepVerifier.create(cacheConnection.flatMapMany(Connection::close)).verifyComplete();
      logger.warn("Release cache connection");
    }
  }
}