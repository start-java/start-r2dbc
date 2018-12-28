package tech.simter.start.r2dbc.po;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * @author RJ
 */
@Table("dream")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dream {
  @Id
  private Integer id;
  private String name;
  private LocalDateTime createOn;
}