package tech.simter.start.springdatar2dbc.cfg;

import io.r2dbc.spi.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.FileCopyUtils;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static io.r2dbc.spi.ConnectionFactoryOptions.*;
import static org.springframework.boot.jdbc.DataSourceInitializationMode.NEVER;

/**
 * Auto config a r2dbc {@link ConnectionFactory} instance base on a {@link R2dbcProperties} config
 * through by property 'spring.datasource' config.
 * <p>
 * Use <a href="https://r2dbc.io/spec/0.8.0.RELEASE/spec/html/#connections.factory.discovery">R2dbc ConnectionFactory Discovery Mechanism</a>.
 *
 * @author RJ
 */
@Configuration
public class ConnectionFactoryConfiguration {
  private final static Logger logger = LoggerFactory.getLogger(ConnectionFactoryConfiguration.class);
  private final R2dbcProperties properties;

  @Autowired
  public ConnectionFactoryConfiguration(R2dbcProperties properties) {
    this.properties = properties;
    logger.debug("R2dbcProperties={}", properties);
  }

  /**
   * See https://r2dbc.io/spec/0.8.0.RELEASE/spec/html/#connections.factory.options
   * <p>
   * 1. https://github.com/r2dbc/r2dbc-h2 <br>
   * 2. https://github.com/r2dbc/r2dbc-postgres <br>
   * 3. https://github.com/r2dbc/r2dbc-mssql <br>
   * 4. https://github.com/mirromutth/r2dbc-mysql
   */
  @Bean
  public ConnectionFactory connectionFactory() {
    Builder builder = ConnectionFactoryOptions.builder();
    if (properties.getProtocol() != null) builder.option(PROTOCOL, properties.getProtocol());
    if (properties.getPlatform() != null) builder.option(DRIVER, properties.getPlatform());
    if (properties.getName() != null) builder.option(DATABASE, properties.getName());
    if (properties.getHost() != null) builder.option(HOST, properties.getHost());
    if (properties.getPort() != null) builder.option(PORT, properties.getPort());
    if (properties.getUsername() != null) builder.option(USER, properties.getUsername());
    if (properties.getPassword() != null) builder.option(PASSWORD, properties.getPassword());

    return ConnectionFactories.get(builder.build());
  }

//  @Bean
//  public ConnectionFactoryInitializer initializer(ConnectionFactory connectionFactory) {
//    ConnectionFactoryInitializer initializer = new ConnectionFactoryInitializer();
//
//    initializer.setConnectionFactory(connectionFactory);
//    initializer.setDatabasePopulator(new CompositeDatabasePopulator(
//      new ResourceDatabasePopulator(new ClassPathResource("sql/" + properties.getPlatform() + "/schema.sql"))
//    ));
//
//    return initializer;
//  }

  // initial database by execute SQL script through R2dbcProperties.schema|data config
  @EventListener(ContextRefreshedEvent.class)
  public void onApplicationEvent() {
    if (properties.getInitializationMode() == null || properties.getInitializationMode() == NEVER)
      return;
    ResourceLoader resourcePatternResolver = new PathMatchingResourcePatternResolver();

    // 1. concat schema and data
    List<String> sqlResources = new ArrayList<>();
    if (properties.getSchema() != null) sqlResources.addAll(properties.getSchema());
    if (properties.getData() != null) sqlResources.addAll(properties.getData());
    if (sqlResources.isEmpty()) return;
    StringBuilder sql = new StringBuilder();
    Map<String, String> scriptContents = new LinkedHashMap<>();
    for (int i = 0; i < sqlResources.size(); i++) {
      String resourcePath = sqlResources.get(i);
      //logger.info("Load script from {}", resourcePath);
      String scriptContent = loadSql(resourcePath, resourcePatternResolver);
      scriptContents.put(resourcePath, scriptContent);
      sql.append("-- copy from ").append(resourcePath).append("\r\n\r\n")
        .append(scriptContent);
      if (i < sqlResources.size() - 1) sql.append("\r\n\r\n");
    }

    // 2. execute sql one by one
    logger.warn("Executing spring.datasource.schema|data scripts to database");
    Mono.from(connectionFactory().create())
      .flatMapMany(connection -> Mono.from(connection.beginTransaction())
        .thenMany(executeAllSql(connection, scriptContents))
        .delayUntil(t -> connection.commitTransaction())
        .onErrorResume(t -> Mono.from(connection.rollbackTransaction()).then(Mono.error(t)))
      )
      .blockLast(Duration.ofSeconds(10));
  }

  private Flux<Integer> executeAllSql(Connection connection, Map<String, String> allSql) {
    List<Flux<Integer>> sources = new ArrayList<>();
    int i = 0, len = allSql.size();
    for (Map.Entry<String, String> e : allSql.entrySet()) {
      int j = ++i;
      sources.add(
        executeSql(connection, e.getValue())
          .doOnComplete(() -> logger.info("{}/{} Success executed script {}", j, len, e.getKey()))
          .doOnError(t -> logger.warn("{}/{} Failed executed script {}", j, len, e.getKey()))
      );
    }
    return Flux.concat(sources);
  }

  private Flux<Integer> executeSql(Connection connection, String sql) {
    return Flux
      .from(connection.createStatement(sql).execute())
      .flatMap(Result::getRowsUpdated);
  }

  private String loadSql(String resourcePath, ResourceLoader resourcePatternResolver) {
    try {
      return FileCopyUtils.copyToString(new InputStreamReader(
        resourcePatternResolver.getResource(resourcePath).getInputStream(), StandardCharsets.UTF_8
      ));
    } catch (IOException e) {
      throw new RuntimeException(e.getMessage(), e);
    }
  }
}