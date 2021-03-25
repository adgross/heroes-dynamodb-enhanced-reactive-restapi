package io.github.adgross.heroes.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class HeroNotFoundException extends Exception {

  public HeroNotFoundException(String id) {
    super(String.format("Hero with id %s not found", id));
  }
}
