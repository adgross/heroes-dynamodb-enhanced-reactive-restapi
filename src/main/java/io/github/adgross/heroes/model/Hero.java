package io.github.adgross.heroes.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hero {

  private String id;

  @NotBlank
  private String name;

  @NotBlank
  private String universe;

  @PositiveOrZero
  private int films;
}
