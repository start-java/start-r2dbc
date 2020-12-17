package tech.simter.start.springdatar2dbc.cfg;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.convert.ConverterBuilder;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.connectionfactory.R2dbcTransactionManager;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import org.springframework.transaction.ReactiveTransactionManager;
import tech.simter.start.springdatar2dbc.PeopleRepository;
import tech.simter.start.springdatar2dbc.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author RJ
 */
@Configuration
@EnableR2dbcRepositories(basePackageClasses = PeopleRepository.class)
@EnableConfigurationProperties(R2dbcProperties.class)
@Import(ConnectionFactoryConfiguration.class)
public class R2dbcConfiguration extends AbstractR2dbcConfiguration {
  private final ConnectionFactory connectionFactory;

  @Autowired
  public R2dbcConfiguration(ConnectionFactory connectionFactory) {
    this.connectionFactory = connectionFactory;
  }

  @Override
  public ConnectionFactory connectionFactory() {
    return this.connectionFactory;
  }

  @Override
  protected List<Object> getCustomConverters() {
    Set<GenericConverter> converters = ConverterBuilder
      .writing(Status.class, String.class, Enum::name)
      .andReading(Status::valueOf)
      .getConverters();
    return new ArrayList<>(converters);
  }

  @Bean
  ReactiveTransactionManager transactionManager(ConnectionFactory connectionFactory) {
    return new R2dbcTransactionManager(connectionFactory);
  }
}