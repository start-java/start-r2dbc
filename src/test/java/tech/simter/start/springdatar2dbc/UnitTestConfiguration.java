package tech.simter.start.springdatar2dbc;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import tech.simter.start.springdatar2dbc.cfg.R2dbcConfiguration;

/**
 * @author RJ
 */
@Configuration
@Import(R2dbcConfiguration.class)
public class UnitTestConfiguration {
}