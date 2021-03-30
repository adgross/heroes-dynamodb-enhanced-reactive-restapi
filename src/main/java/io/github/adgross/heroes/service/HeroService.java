package io.github.adgross.heroes.service;


import io.github.adgross.heroes.model.Hero;
import io.github.adgross.heroes.model.HeroRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface HeroService {
  Flux<Hero> listHeroes();

  Mono<Hero> findById(String id);

  Mono<Hero> create(HeroRequest hero);

  Mono<Hero> forceCreate(String id, HeroRequest hero);

  Mono<Hero> update(String id, HeroRequest hero);

  Mono<Hero> deleteById(String id);
}
