package io.github.adgross.heroes.repository;

import io.github.adgross.heroes.model.Hero;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

/**
 * Integration test.
 * Only tests the HeroRepository.
 * Note: need a working dynamodb connection, you can use a dynamodb-local.
 */
@SpringBootTest(classes = HeroRepository.class)
public class HeroRepositoryTest {

  @Autowired
  HeroRepository heroRepository;

  @Test
  public void putAndGet() {
    String id = "123";
    Hero hero = new Hero(id, "Sonic", "Sonic", 1);
    testPut(hero);
    testGet(id, hero);
  }

  @Test
  public void putAndUpdateAndGet() {
    String id = "1234";
    Hero hero        = new Hero(id, "Tail", "Sonic", 1);
    Hero heroUpdated = new Hero(id, "Tails", "Sonic", 1);
    testPut(hero);
    testUpdate(heroUpdated);
    testGet(id, heroUpdated);
  }

  @Test
  public void putAndDeleteByHeroAndGet() {
    String id = "12345";
    Hero hero = new Hero(id, "Eggman", "Sonic", 1);
    testPut(hero);
    testDeleteByHero(hero);
    testGetNotExist(id);
  }

  @Test
  public void putAndDeleteByIdAndGet() {
    String id = "123456";
    Hero hero = new Hero(id, "Eggman Nega", "Sonic", 0);
    testPut(hero);
    testDeleteById(id, hero);
    testGetNotExist(id);
  }

  @Test
  public void resetPutAndGetAll() {
    resetDb();

    Hero hero1 = new Hero("1", "Sonic",  "Sonic", 1);
    Hero hero2 = new Hero("2", "Shadow", "Sonic", 0);
    Hero hero3 = new Hero("3", "Silver", "Sonic", 0);

    testPut(hero1);
    testPut(hero2);
    testPut(hero3);

    var getAll = heroRepository.getAll();
    StepVerifier.create(getAll)
        .expectSubscription()
        .recordWith(ArrayList::new)
        .expectNextCount(3)
        .expectRecordedMatches(h -> h.contains(hero1) && h.contains(hero2) && h.contains(hero3))
        .verifyComplete();
  }

  @Test
  public void resetPut5000AndGetAll() {
    resetDb();

    int limit = 5000;
    var put5000 = Flux.range(1, limit)
        .doOnNext(i -> testPut(new Hero(String.valueOf(i), "clone", "test", 1)))
        .then();
    var getAll = heroRepository.getAll();

    StepVerifier.create(put5000)
        .verifyComplete();
    StepVerifier.create(getAll)
        .recordWith(ArrayList::new)
        .thenRequest(limit)
        .expectNextCount(limit)
        .expectRecordedMatches(heroes -> heroes.size() == limit)
        .verifyComplete();
  }

  @Test
  public void resetPutAndCancelGetAll() {
    resetDb();

    var putSome = Flux.range(1, 10)
        .doOnNext(i -> testPut(new Hero(String.valueOf(i), "clone", "test", 1)))
        .then();
    var getAll = heroRepository.getAll();

    StepVerifier.create(putSome)
        .verifyComplete();
    StepVerifier.create(getAll)
        .thenRequest(1)
        .expectNextCount(1)
        .thenRequest(5)
        .expectNextCount(5)
        .thenCancel()
        .verify();
  }

  @Test
  public void resetDb() {
    var deleteAll = heroRepository.resetTable();

    StepVerifier.create(deleteAll)
        .verifyComplete();
  }

  // --- --- --- --- --- ---
  // Helper methods
  // --- --- --- --- --- ---

  private void testGet(String id, Hero hero) {
    var get = heroRepository.get(id);
    StepVerifier.create(get)
        .expectSubscription()
        .expectNext(hero)
        .verifyComplete();
  }

  private void testGetNotExist(String id) {
    var get = heroRepository.get(id);
    StepVerifier.create(get)
        .expectSubscription()
        .verifyComplete();
  }

  private void testPut(Hero hero) {
    var put = heroRepository.put(hero);
    StepVerifier.create(put)
        .expectSubscription()
        .verifyComplete();
  }

  private void testUpdate(Hero hero) {
    var update = heroRepository.update(hero);
    StepVerifier.create(update)
        .expectSubscription()
        .expectNext(hero)
        .verifyComplete();
  }

  private void testDeleteById(String id, Hero hero) {
    var delete = heroRepository.delete(id);
    StepVerifier.create(delete)
        .expectSubscription()
        .expectNext(hero)
        .verifyComplete();
  }

  private void testDeleteByHero(Hero hero) {
    var delete = heroRepository.delete(hero);
    StepVerifier.create(delete)
        .expectSubscription()
        .expectNext(hero)
        .verifyComplete();
  }
}
