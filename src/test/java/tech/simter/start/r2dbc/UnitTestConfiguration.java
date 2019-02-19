package tech.simter.start.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

/**
 * @author RJ
 */
@Configuration
@EnableR2dbcRepositories
@EnableConfigurationProperties
@ComponentScan({
  "tech.simter.start.r2dbc", // this module
  "tech.simter.r2dbc" // auto generate connectionFactory bean by maven dependency (from simter-r2dbc-ext)
})
public class UnitTestConfiguration extends AbstractR2dbcConfiguration {
  private final ConnectionFactory connectionFactory;

  public UnitTestConfiguration(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  @Override
  public ConnectionFactory connectionFactory() {
    return this.connectionFactory;
  }
}