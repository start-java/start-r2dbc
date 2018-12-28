package tech.simter.start.r2dbc;

import io.r2dbc.postgresql.PostgresqlConnectionConfiguration;
import io.r2dbc.postgresql.PostgresqlConnectionFactory;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author RJ
 */
@Configuration
public class ConnectionFactoryConfiguration {
  @Bean
  ConnectionFactory connectionFactory() {
    PostgresqlConnectionConfiguration config = PostgresqlConnectionConfiguration.builder()
      .database("test")
      .host("localhost")
      .username("test")
      .password("password")
      .build();
    return new PostgresqlConnectionFactory(config);
  }
}

