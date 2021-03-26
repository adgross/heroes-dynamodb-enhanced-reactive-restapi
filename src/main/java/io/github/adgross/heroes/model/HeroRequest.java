package io.github.adgross.heroes.model;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HeroRequest {

  @NotBlank
  @Size(min = 2, max  = 256)
  private String name;

  @NotBlank
  @Size(min = 2, max = 128)
  private String universe;

  @PositiveOrZero
  private int films;
}
