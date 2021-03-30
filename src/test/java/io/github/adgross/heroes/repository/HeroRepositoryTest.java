package io.github.adgross.heroes.repository;

import io.github.adgross.heroes.model.Hero;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
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
  public void deleteAllAndPutAndGetAll() {
    var clearDb = heroRepository.getAll()
        .doOnNext(hero -> heroRepository.delete(hero));
    StepVerifier.create(clearDb)
        .thenConsumeWhile(hero -> true)
        .verifyComplete();

    String id1 = "1";
    String id2 = "2";
    String id3 = "3";
    Hero hero1 = new Hero(id1, "Sonic", "Sonic", 1);
    Hero hero2 = new Hero(id2, "Shadow", "Sonic", 0);
    Hero hero3 = new Hero(id3, "Silver", "Sonic", 0);

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
