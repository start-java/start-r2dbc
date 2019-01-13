package tech.simter.start.springdatar2dbc;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import tech.simter.r2dbc.R2dbcConfiguration;

/**
 * @author RJ
 */
@Configuration
@EnableR2dbcRepositories
@EnableConfigurationProperties    // for tech.simter.r2dbc.R2dbcProperties
@Import(R2dbcConfiguration.class) // auto generate connectionFactory bean by maven dependency (from simter-r2dbc-ext)
public class UnitTestConfiguration extends AbstractR2dbcConfiguration {
  private final ConnectionFactory connectionFactory;

  @Autowired
  public UnitTestConfiguration(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  @Override
  public ConnectionFactory connectionFactory() {
    return this.connectionFactory;
  }
}