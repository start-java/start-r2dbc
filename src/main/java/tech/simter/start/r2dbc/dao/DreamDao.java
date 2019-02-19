package tech.simter.start.r2dbc.dao;

import reactor.core.publisher.Mono;
import tech.simter.start.r2dbc.po.Dream;

/**
 * @author RJ
 */
public interface DreamDao {
  Mono<Void> create(Dream dream);

  Mono<Void> clean();

  /**
   * Return {@link Mono#empty} if not exists.
   */
  Mono<String> getNameById(Integer id);
}