package tech.simter.start.r2dbc;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * @author RJ
 */
@Configuration
@EnableConfigurationProperties
@ComponentScan({
  "tech.simter.r2dbc" // auto generate connectionFactory bean by maven dependency (from simter-r2dbc-ext)
})
public class ConnectionFactoryConfiguration {
}