package tech.simter.start.springdatar2dbc.issue49;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import tech.simter.start.springdatar2dbc.People;
import tech.simter.start.springdatar2dbc.PeopleRepository;
import tech.simter.start.springdatar2dbc.Status;
import tech.simter.start.springdatar2dbc.UnitTestConfiguration;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

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
    // prepare data
    People po = new People();
    po.setId(UUID.randomUUID().toString());
    po.setName("simter");
    po.setStatus(Status.Enabled);

    // save it
    repository.save(po)
      .as(StepVerifier::create)
      .assertNext(p -> {
        assertEquals(p.getId(), po.getId());
        assertEquals(p.getName(), po.getName());
      })
      .verifyComplete();

    // verify saved
    assertNotNull(po.getId());
    repository.findById(po.getId())
      .as(StepVerifier::create)
      .assertNext(p -> {
        assertEquals(p.getId(), po.getId());
        assertEquals(p.getName(), po.getName());
      })
      .verifyComplete();
  }
}