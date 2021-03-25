package io.github.adgross.heroes.service;


import io.github.adgross.heroes.model.Hero;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HeroService {
  public Flux<Hero> listHeroes();

  public Mono<Hero> findById(String id);

  public Mono<Hero> save(Hero hero);

  public Mono<Hero> deleteById(String id);
}
