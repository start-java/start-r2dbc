package tech.simter.start.springdatar2dbc;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

/**
 * @author RJ
 */
public interface PeopleRepository extends ReactiveCrudRepository<People, String> {
}