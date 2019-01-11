package tech.simter.start.r2dbc.spring.standardcrud;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import tech.simter.start.r2dbc.UnitTestConfiguration;
import tech.simter.start.r2dbc.spring.standardcurd.People;
import tech.simter.start.r2dbc.spring.standardcurd.PeopleRepository;

import java.util.UUID;

/**
 * @author RJ
 */
@SpringBootTest(classes = UnitTestConfiguration.class)
@ExtendWith(SpringExtension.class)
class SaveMethodImplTest {
  @Autowired
  private PeopleRepository repository;

  @BeforeEach
  void clean() {
    StepVerifier.create(repository.deleteAll()).verifyComplete();
  }

  @Test
  void test() {
    // save it
    People po = new People();
    po.setId(UUID.randomUUID().toString());
    po.setName("simter");
    StepVerifier.create(repository.save(po))
      .expectNext(po)
      .verifyComplete();

    // verify saved
    StepVerifier.create(repository.findById(po.getId()))
      .expectNext(po)
      .verifyComplete();
  }
}