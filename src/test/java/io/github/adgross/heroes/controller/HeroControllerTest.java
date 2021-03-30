package io.github.adgross.heroes.controller;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_NDJSON;
import static org.springframework.http.MediaType.APPLICATION_NDJSON_VALUE;

import io.github.adgross.heroes.model.Hero;
import io.github.adgross.heroes.model.HeroRequest;
import io.github.adgross.heroes.service.HeroService;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

/**
 * Validation and response testing for HeroController.
 */
@WebFluxTest
public class HeroControllerTest {

  @MockBean
  private HeroService heroService;

  @Autowired
  private WebTestClient client;

  @Test
  public void findByIdWithValidId() {
    String requestId = UUID.randomUUID().toString();

    Hero serverHero = new Hero(requestId, "Sonic", "Sonic", 1);
    String responseHero = "{\"id\":\"" + requestId + "\","
        + "\"name\":\"Sonic\",\"universe\":\"Sonic\",\"films\":1}";

    Mockito.when(heroService.findById(Mockito.any(String.class)))
        .thenReturn(Mono.just(serverHero));

    client.get()
        .uri("/api/v1/heroes/{id}", requestId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(APPLICATION_JSON_VALUE)
        .expectBody().json(responseHero);
  }

  @Test
  public void findByIdWithInvalidId() {
    String requestId = "999";

    Mockito.when(heroService.findById(Mockito.any(String.class)))
        .thenReturn(Mono.empty());

    client.get()
        .uri("/api/v1/heroes/{id}", requestId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  public void createWithValidHero() {
    String requestHero = "{\"name\":\"Sonic\",\"universe\":\"Sonic\",\"films\":1}";
    Hero serverHero = new Hero("1", "Sonic", "Sonic", 1);
    String responseHero = "{\"id\":\"1\",\"name\":\"Sonic\",\"universe\":\"Sonic\",\"films\":1}";

    Mockito.when(heroService.create(Mockito.any(HeroRequest.class)))
        .thenReturn(Mono.just(serverHero));

    client.post()
        .uri("/api/v1/heroes")
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyValue(requestHero)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isCreated()
        .expectBody().json(responseHero);
  }

  @Test
  public void createWithInvalidHero() {
    List<String> requestHeroes = List.of(
        "{\"name\":\"Sonic\",\"universe\":\"Sonic\",\"films\":-1}", // negative films
        "{\"name\":\"\"     ,\"universe\":\"Sonic\",\"films\": 1}", // empty name
        "{\"name\":\"Sonic\",\"universe\":\"\"     ,\"films\": 1}"  // empty universe
    );

    for (var request : requestHeroes) {
      client.post()
          .uri("/api/v1/heroes")
          .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
          .bodyValue(request)
          .accept(APPLICATION_JSON)
          .exchange()
          .expectStatus().isBadRequest();
    }
  }

  @Test
  public void deleteWithValidIdRegistered() {
    String requestId = UUID.randomUUID().toString();
    Hero serverHero = new Hero(requestId, "Sonic", "Sonic", 1);

    Mockito.when(heroService.deleteById(Mockito.any(String.class)))
        .thenReturn(Mono.just(serverHero));

    client.delete()
        .uri("/api/v1/heroes/{id}", requestId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isNoContent()
        .expectBody()
        .jsonPath("$.id").isEqualTo(requestId);
  }

  @Test
  public void deleteWithValidIdNotRegistered() {
    String requestId = UUID.randomUUID().toString();

    Mockito.when(heroService.deleteById(Mockito.any(String.class)))
        .thenReturn(Mono.empty());

    client.delete()
        .uri("/api/v1/heroes/{id}", requestId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  public void deleteWithInvalidId() {
    String requestId = "999";

    client.delete()
        .uri("/api/v1/heroes/{id}", requestId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  public void updateWithValidIdAndHero() {
    String requestHero = "{\"name\":\"Sonic\",\"universe\":\"Sonic\",\"films\":1}";
    String requestId = UUID.randomUUID().toString();
    Hero serverHero = new Hero(requestId, "Sonic", "Sonic", 1);

    Mockito.when(heroService.update(Mockito.any(String.class), Mockito.any(HeroRequest.class)))
        .thenReturn(Mono.just(serverHero));

    client.put()
        .uri("/api/v1/heroes/{id}", requestId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyValue(requestHero)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.id").isEqualTo(requestId);
  }

  @Test
  public void updateWithValidIdAndInvalidHero() {
    String requestId = UUID.randomUUID().toString();
    List<String> requestHeroes = List.of(
        "{\"name\":\"Sonic\",\"universe\":\"Sonic\",\"films\":-1}", // negative films
        "{\"name\":\"\"     ,\"universe\":\"Sonic\",\"films\": 1}", // empty name
        "{\"name\":\"Sonic\",\"universe\":\"\"     ,\"films\": 1}"  // empty universe
    );

    for (var requestHero : requestHeroes) {
      client.put()
          .uri("/api/v1/heroes/{id}", requestId)
          .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
          .bodyValue(requestHero)
          .accept(APPLICATION_JSON)
          .exchange()
          .expectStatus().isBadRequest();
    }
  }

  @Test
  public void updateWithInvalidIdAndHero() {
    String requestId = "9999";
    String requestHero = "{\"name\":\"Sonic\",\"universe\":\"Sonic\",\"films\":-1}";

    client.put()
        .uri("/api/v1/heroes/{id}", requestId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyValue(requestHero)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  public void updateWithInvalidIdAndValidHero() {
    String requestId = "9999";
    String requestHero = "{\"name\":\"Sonic\",\"universe\":\"Sonic\",\"films\":1}";

    client.put()
        .uri("/api/v1/heroes/{id}", requestId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyValue(requestHero)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isBadRequest();
  }

  @Test
  public void getAllItems() {
    var heroes = Flux.just(
        new Hero("00000000-0000-0000-0000-000000000011", "Sonic", "Sonic", 1),
        new Hero("00000000-0000-0000-0000-000000000012", "Tails", "Sonic", 1),
        new Hero("00000000-0000-0000-0000-000000000013", "Knuckles", "Sonic", 1)
    );

    Mockito.when(heroService.listHeroes()).thenReturn(heroes);

    client.get()
        .uri("/api/v1/heroes/")
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(APPLICATION_JSON_VALUE)
        .expectBodyList(Hero.class)
        .hasSize(3);
  }

  @Test
  public void getAllItemsStream() {
    var heroList = List.of(
        new Hero("00000000-0000-0000-0000-000000000021", "Sonic", "Sonic", 1),
        new Hero("00000000-0000-0000-0000-000000000022", "Tails", "Sonic", 1),
        new Hero("00000000-0000-0000-0000-000000000023", "Knuckles", "Sonic", 1),
        new Hero("00000000-0000-0000-0000-000000000031", "Shadow", "Sonic", 0),
        new Hero("00000000-0000-0000-0000-000000000032", "Silver", "Sonic", 0)
    );
    var heroes = Flux.fromStream(heroList.stream());

    Mockito.when(heroService.listHeroes()).thenReturn(heroes);

    var result = client.get()
        .uri("/api/v1/heroes/items")
        .header(CONTENT_TYPE, APPLICATION_NDJSON_VALUE)
        .accept(APPLICATION_NDJSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(APPLICATION_NDJSON_VALUE)
        .returnResult(Hero.class).getResponseBody().log();

    StepVerifier.create(result)
        .expectSubscription()
        .expectNext(heroList.get(0))
        .expectNext(heroList.get(1))
        .thenCancel()
        .verify();
  }

}
