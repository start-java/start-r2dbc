package tech.simter.start.r2dbc.spring.standardcurd;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author RJ
 */
public interface PeopleRepository extends ReactiveCrudRepository<People, String> {
}