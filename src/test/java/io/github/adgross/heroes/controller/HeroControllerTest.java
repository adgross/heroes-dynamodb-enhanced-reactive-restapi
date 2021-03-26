package io.github.adgross.heroes.controller;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.github.adgross.heroes.model.Hero;
import io.github.adgross.heroes.model.HeroRequest;
import io.github.adgross.heroes.service.HeroService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

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

}
