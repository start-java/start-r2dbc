package tech.simter.start.r2dbc.dao.impl;

import io.r2dbc.spi.Connection;
import io.r2dbc.spi.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tech.simter.start.r2dbc.dao.DreamDao;
import tech.simter.start.r2dbc.po.Dream;

import java.sql.Timestamp;

/**
 * @author RJ
 */
@Repository
public class DreamDaoImpl implements DreamDao {
  @Autowired
  private ConnectionFactory connectionFactory;

  private Mono<Connection> connection() {
    return Mono.from(connectionFactory.create());
  }

  @Override
  public Mono<Void> clean() {
    return this.connection()
      .flatMapMany(c -> c.createStatement("delete from dream").execute())
      .then();
  }

  @Override
  public Mono<Void> create(Dream dream) {
    return this.connection()
      .flatMapMany(c ->
        c.createStatement("insert into dream(id, name, create_on) values($1, $2, $3)")
          .bind("$1", dream.getId())
          .bind("$2", dream.getName())
          // convert to Timestamp otherwise throw 'IllegalArgumentException: Cannot encode parameter of type java.time.LocalDateTime'
          .bind("$3", Timestamp.valueOf(dream.getCreateOn()))
          .add()
          .execute()
      )
      .switchMap(x -> Flux.just(dream))
      //.collectList()
      .then();
  }

  @Override
  public Mono<String> getNameById(Integer id) {
    return this.connection()
      .flatMap(c ->
        Mono.from(c.createStatement("select name as name from dream  where id = $1")
          .bind("$1", id)
          .execute()
        ).flatMap(result ->
          Mono.from(result.map((row, rowMetadata) -> row.get("name", String.class)))
        )
      );
  }
}