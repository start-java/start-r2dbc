package tech.simter.start.r2dbc.client;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.Result;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * For test
 *
 * @author RJ
 */
class Dao {
  private Connection connection;

  Dao(Connection connection) {
    this.connection = connection;
  }

  Mono<String> getNameById(Integer id) {
    return Flux.from(connection
      .createStatement("select name from t where id=$1")
      .bind("$1", id)
      .execute())
      .flatMap(result -> result.map((row, metadata) -> row.get("name", String.class)))
      .next();
  }

  Mono<Integer> updateName(Integer id, String name) {
    return Mono.from(connection
      .createStatement("update t set name = $2 where id=$1")
      .bind("$1", id)
      .bind("$2", name)
      .execute())
      .flatMapMany(Result::getRowsUpdated)
      .next();
  }

  Flux<Integer> findAllId() {
    return Flux.from(connection
      .createStatement("select id from t")
      .execute())
      .flatMap(result -> result.map((row, metadata) -> row.get("id", Integer.class)));
  }
}