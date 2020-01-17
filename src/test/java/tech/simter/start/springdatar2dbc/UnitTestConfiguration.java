package tech.simter.start.springdatar2dbc;

import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.convert.converter.GenericConverter;
import org.springframework.data.convert.ConverterBuilder;
import org.springframework.data.r2dbc.config.AbstractR2dbcConfiguration;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import tech.simter.r2dbc.R2dbcConfiguration;

import java.util.List;
import java.util.Set;

/**
 * @author RJ
 */
@Configuration
@EnableR2dbcRepositories
//@EnableConfigurationProperties    // for tech.simter.r2dbc.R2dbcProperties
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

  @Override
  protected List<Object> getCustomConverters() {
    Set<GenericConverter> converters = ConverterBuilder
      .writing(Status.class, String.class, status -> status.name())
      .andReading(Status::valueOf)
      .getConverters();
    return super.getCustomConverters();
  }

  @Bean
  public StatusWriteConverter statusWriteConverter() {
    return StatusWriteConverter.Instance;
  }

  @Bean
  public StatusReadConverter statusReadConverter() {
    return StatusReadConverter.Instance;
  }
}