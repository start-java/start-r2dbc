package tech.simter.start.springdatar2dbc;

import org.springframework.data.annotation.Id;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author RJ
 */
@Table("people")
public class People implements Persistable<String> {
  @Id
  private String id;
  private String name;
  private Status status;

  @Override
  public boolean isNew() {
    return true;
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Status getStatus() {
    return status;
  }

  public void setStatus(Status status) {
    this.status = status;
  }
}