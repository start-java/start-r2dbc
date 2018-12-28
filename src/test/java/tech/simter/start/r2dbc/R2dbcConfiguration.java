package tech.simter.start.r2dbc;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.function.DatabaseClient;

/**
 * @author RJ
 */
@Configuration
@Import(ConnectionFactoryConfiguration.class)
public class R2dbcConfiguration {
  @Bean
  DatabaseClient databaseClient(ConnectionFactory connectionFactory) {
    return DatabaseClient.create(connectionFactory);
  }
}