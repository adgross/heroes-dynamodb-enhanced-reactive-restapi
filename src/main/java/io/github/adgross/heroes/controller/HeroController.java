package io.github.adgross.heroes.controller;

import io.github.adgross.heroes.exception.HeroNotFoundException;
import io.github.adgross.heroes.model.Hero;
import io.github.adgross.heroes.model.HeroRequest;
import io.github.adgross.heroes.service.HeroService;
import java.util.UUID;
import javax.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

  @GetMapping(value = "/items", produces = MediaType.APPLICATION_NDJSON_VALUE)
  @ResponseStatus(HttpStatus.OK)
  public Flux<Hero> items() {
    log.info("Requesting list stream of all heroes");
    return heroService.listHeroes();
  }

  @GetMapping("/{uuid}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Hero> findById(@PathVariable @Valid UUID uuid) {
    log.info("Requesting the hero with id {}", uuid);
    return heroService.findById(uuid.toString())
        .switchIfEmpty(Mono.error(new HeroNotFoundException(uuid.toString())));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Hero> create(@RequestBody @Valid HeroRequest hero) {
    log.info("Creating a new hero");
    return heroService.create(hero);
  }

  /**
   * Create the Hero with given ID, overwrite if already exists.
   */
  @PostMapping("/{uuid}")
  @ResponseStatus(HttpStatus.CREATED)
  public Mono<Hero> forceCreate(@PathVariable @Valid UUID uuid,
                                @RequestBody @Valid HeroRequest hero) {
    log.info("Creating hero with id {}", uuid);
    return heroService.forceCreate(uuid.toString(), hero);
  }

  @PutMapping("/{uuid}")
  @ResponseStatus(HttpStatus.OK)
  public Mono<Hero> update(@PathVariable @Valid UUID uuid, @RequestBody @Valid HeroRequest hero) {
    log.info("Updating the hero with id {}", uuid);
    return heroService.update(uuid.toString(), hero)
        .switchIfEmpty(Mono.error(new HeroNotFoundException(uuid.toString())));
  }

  @DeleteMapping("/{uuid}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  public Mono<Hero> deleteById(@PathVariable @Valid UUID uuid) {
    log.info("Deleting the hero with id {}", uuid);
    return heroService.deleteById(uuid.toString())
        .switchIfEmpty(Mono.error(new HeroNotFoundException(uuid.toString())));
  }

}
