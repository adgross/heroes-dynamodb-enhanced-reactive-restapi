package io.github.adgross.heroes.controller;

import io.github.adgross.heroes.exception.HeroNotFoundException;
import io.github.adgross.heroes.model.Hero;
import io.github.adgross.heroes.service.HeroService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("/api/v1/heroes")
public class HeroController {

  @Autowired
  HeroService heroService;

  @GetMapping
  @ResponseStatus(HttpStatus.OK)
  public Flux<Hero> getAllItems() {
    log.info("Requesting the list of all heroes");
    return heroService.listHeroes();
  }

  @GetMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Hero> findById(@PathVariable String id) {
    log.info("Requesting the hero with id {}", id);
    return heroService.findById(id)
        .switchIfEmpty(Mono.error(new HeroNotFoundException(id)));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Hero> create(@RequestBody Hero hero) {
    log.info("Creating a new hero");
    return heroService.create(hero);
  }

  @PutMapping("/{id}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Hero> update(@PathVariable String id, @RequestBody Hero hero) {
    log.info("Updating the hero with id {}", id);
    return heroService.update(id, hero)
        .switchIfEmpty(Mono.error(new HeroNotFoundException(id)));
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Hero> deleteById(@PathVariable String id) {
    log.info("Deleting the hero with id {}", id);
    return heroService.deleteById(id)
        .switchIfEmpty(Mono.error(new HeroNotFoundException(id)));
  }

}
