package tech.simter.start.r2dbc.dao.impl;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.test.StepVerifier;
import tech.simter.start.r2dbc.R2dbcConfiguration;
import tech.simter.start.r2dbc.dao.DreamDao;
import tech.simter.start.r2dbc.po.Dream;

import java.time.LocalDateTime;

/**
 * @author RJ
 */
@SpringBootTest(classes = {DreamDaoImpl.class, R2dbcConfiguration.class})
@ExtendWith(SpringExtension.class)
class GetNameByIdMethodImplTest {
  @Autowired
  private DreamDao dao;

  @BeforeEach
  void clean() {
    StepVerifier.create(dao.clean()).verifyComplete();
  }

  private void save(Dream dream) {
    StepVerifier.create(dao.saveOne(dream)).expectNextCount(0).verifyComplete();
  }

  @Test
  void test() {
    Integer id = 9;
    // found nothing
    StepVerifier.create(dao.getNameById(id)).verifyComplete();

    // init some data
    Dream dream = new Dream(id, "test1", LocalDateTime.now());
    save(dream);

    // verify found something
    StepVerifier.create(dao.getNameById(id))
      .expectNext(dream.getName())
      .verifyComplete();
  }
}