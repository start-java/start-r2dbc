package tech.simter.start.r2dbc.spring.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import tech.simter.start.r2dbc.po.Dream;
import tech.simter.start.r2dbc.UnitTestConfiguration;

import java.time.LocalDateTime;

/**
 * @author RJ
 */
@SpringBootTest(classes = UnitTestConfiguration.class)
@ExtendWith(SpringExtension.class)
class FindAllNameMethodImplTest {
  @Autowired
  private DreamRepository repository;

  @BeforeEach
  void clean() {
    StepVerifier.create(repository.deleteAll()).verifyComplete();
  }

  private void save(Dream dream) {
    StepVerifier.create(repository.save(dream)).expectNextCount(1).verifyComplete();
  }

  @Test
  void test() {
    // init some data
    Dream dream = new Dream(1, "test", LocalDateTime.now());
    save(dream);

    // verify found something
    StepVerifier.create(repository.findAllName())
      .expectNext(dream.getName())
      .verifyComplete();
  }
}