package io.github.adgross.heroes.service;

import io.github.adgross.heroes.model.Hero;
import io.github.adgross.heroes.model.HeroRequest;
import io.github.adgross.heroes.repository.HeroRepository;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class HeroServiceImp implements HeroService {

  @Autowired
  HeroRepository heroRepository;

  public Flux<Hero> listHeroes() {
    return heroRepository.getAll();
  }

  public Mono<Hero> findById(String id) {
    return heroRepository.get(id);
  }

  public Mono<Hero> create(HeroRequest hero) {
    Hero newHero = new Hero();
    newHero.setId(UUID.randomUUID().toString());
    newHero.setName(hero.getName());
    newHero.setUniverse(hero.getUniverse());
    newHero.setFilms(hero.getFilms());

    return heroRepository.put(newHero)
        .then(Mono.just(newHero));
  }

  public Mono<Hero> update(String id, HeroRequest hero) {
    Hero updated = new Hero();
    updated.setId(id);
    updated.setName(hero.getName());
    updated.setUniverse(hero.getUniverse());
    updated.setFilms(hero.getFilms());

    return heroRepository.update(updated);
  }

  public Mono<Hero> deleteById(String id) {
    return heroRepository.delete(id);
  }

}
