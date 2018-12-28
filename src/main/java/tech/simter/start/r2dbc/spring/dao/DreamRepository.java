package tech.simter.start.r2dbc.spring.dao;

import org.springframework.data.r2dbc.repository.query.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.simter.start.r2dbc.po.Dream;

/**
 * @author RJ
 */
public interface DreamRepository extends ReactiveCrudRepository<Dream, Integer> {
  /**
   * Return {@link Mono#empty} if not exists.
   */
  @Query("select name from dream where id = $1")
  Mono<String> getNameById(Integer id);

  @Query("select name from dream")
  Flux<String> findAllName();
}