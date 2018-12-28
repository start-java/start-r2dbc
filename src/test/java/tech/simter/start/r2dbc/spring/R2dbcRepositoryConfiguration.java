package tech.simter.start.r2dbc.spring;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import tech.simter.start.r2dbc.ConnectionFactoryConfiguration;

/**
 * @author RJ
 */
@Configuration
@EnableR2dbcRepositories
@Import(ConnectionFactoryConfiguration.class)
@ComponentScan("tech.simter.start.r2dbc.spring")
public class R2dbcRepositoryConfiguration extends AbstractR2dbcConfiguration {
  private final ConnectionFactory connectionFactory;

  public R2dbcRepositoryConfiguration(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  @Override
  public ConnectionFactory connectionFactory() {
    return this.connectionFactory;
  }
}