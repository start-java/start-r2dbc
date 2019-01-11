package tech.simter.start.r2dbc.spring.standardcurd;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author RJ
 */
@Table("people")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class People {
  @Id
  private String id;
  private String name;
}