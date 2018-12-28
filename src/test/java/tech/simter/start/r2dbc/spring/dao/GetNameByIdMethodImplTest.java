package tech.simter.start.r2dbc.spring.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import tech.simter.start.r2dbc.po.Dream;
import tech.simter.start.r2dbc.spring.R2dbcRepositoryConfiguration;

import java.time.LocalDateTime;

/**
 * @author RJ
 */
@SpringBootTest(classes = R2dbcRepositoryConfiguration.class)
@ExtendWith(SpringExtension.class)
class GetNameByIdMethodImplTest {
  @Autowired
  private DreamRepository repository;

  @BeforeEach
  void clean() {
    StepVerifier.create(repository.deleteAll()).verifyComplete();
  }

  private void save(Dream dream) {
    StepVerifier.create(repository.save(dream)).expectNextCount(0).verifyComplete();
  }

  @Test
  void test() {
    Integer id = 9;
    // found nothing
    StepVerifier.create(repository.getNameById(id)).verifyComplete();

    // init some data
    Dream dream = new Dream(id, "test", LocalDateTime.now());
    save(dream);

    // verify found something
    StepVerifier.create(repository.getNameById(id))
      .expectNext(dream.getName())
      .verifyComplete();
  }
}