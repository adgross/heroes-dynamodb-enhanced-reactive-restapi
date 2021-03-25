package io.github.adgross.heroes.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hero {
  private String id;
  private String name;
  private String universe;
  private int films;
}
