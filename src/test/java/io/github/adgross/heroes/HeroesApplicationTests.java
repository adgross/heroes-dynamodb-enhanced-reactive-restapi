package io.github.adgross.heroes;

import static org.springframework.http.HttpHeaders.CONTENT_TYPE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.http.MediaType.APPLICATION_NDJSON;

import io.github.adgross.heroes.model.Hero;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

/**
 * Integration test.
 * Note: need a working dynamodb connection, you can use a dynamodb-local
 */
@SpringBootTest(classes = HeroesApplication.class,
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
class HeroesApplicationTests {

  @Autowired
  private WebTestClient client;

  void forceCreate(String requestId, String requestHero) {
    client.put()
        .uri("/api/v1/heroes/{id}", requestId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyValue(requestHero)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .json(requestHero);
  }

  @Test
  void createHeroes() {
    String requestHero = "{\"name\":\"Sonic\",\"universe\":\"Sonic\",\"films\":1}";
    for (int i = 0; i < 100; i++) {
      client.post()
          .uri("/api/v1/heroes")
          .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
          .bodyValue(requestHero)
          .accept(APPLICATION_JSON)
          .exchange()
          .expectStatus().isCreated()
          .expectBody()
          .jsonPath("$.name").isEqualTo("Sonic");
    }
  }

  @Test
  void forceCreateAndFindById() {
    String requestId = "00000000-0000-0000-0000-000000000000";
    String requestHero = "{\"name\":\"Tails\",\"universe\":\"Sonic\",\"films\":1}";

    // call update with id
    forceCreate(requestId, requestHero);

    // check if it got created
    client.get()
        .uri("/api/v1/heroes/{id}", requestId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(APPLICATION_JSON_VALUE)
        .expectBody()
        .jsonPath("$.id").isEqualTo(requestId)
        .jsonPath("$.name").isEqualTo("Tails")
        .jsonPath("$.universe").isEqualTo("Sonic")
        .jsonPath("$.films").isEqualTo(1);
  }

  /*
   * if update happens without an attribute, dynamodb will delete the attribute in the table
   * validations to prevent this should be tested in controller unit tests
   */
  @Test
  void updateWithAllParameters() {
    String requestHero = "{\"name\":\"Shadow\",\"universe\":\"Sonic\",\"films\":0}";
    String requestId = "11111111-1111-1111-1111-111111111111";

    client.put()
        .uri("/api/v1/heroes/{id}", requestId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .bodyValue(requestHero)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectBody()
        .jsonPath("$.name").isEqualTo("Shadow")
        .jsonPath("$.films").isEqualTo(0);

    client.get()
        .uri("/api/v1/heroes/{id}", requestId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(APPLICATION_JSON_VALUE)
        .expectBody()
        .jsonPath("$.name").isEqualTo("Shadow")
        .jsonPath("$.universe").isEqualTo("Sonic")
        .jsonPath("$.films").isEqualTo(0);
  }

  @Test
  void deleteIdNotExist() {
    String requestId = "88888888-8888-8888-8888-888888888888";

    client.delete()
        .uri("/api/v1/heroes/{id}", requestId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isNotFound();
  }

  @Test
  void deleteIdExist() {
    String requestId = "99999999-9999-9999-9999-999999999999";
    String requestHero = "{\"name\":\"Silver\",\"universe\":\"Sonic\",\"films\":1}";

    // call update with id
    forceCreate(requestId, requestHero);

    client.delete()
        .uri("/api/v1/heroes/{id}", requestId)
        .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
        .accept(APPLICATION_JSON)
        .exchange()
        .expectStatus().isNoContent()
        .expectBody()
        .jsonPath("$.id").isEqualTo(requestId)
        .jsonPath("$.name").isEqualTo("Silver");
  }

  @Test
  void getStream() {
    Map<String, String> requests = Map.of(
        "00000000-0000-0000-0000-000000000001",
        "{\"name\":\"Sonic\",\"universe\":\"Sonic\",\"films\":1}",
        "00000000-0000-0000-0000-000000000002",
        "{\"name\":\"Tails\",\"universe\":\"Sonic\",\"films\":1}",
        "00000000-0000-0000-0000-000000000003",
        "{\"name\":\"Knuckles\",\"universe\":\"Sonic\",\"films\":1}");

    for (var v : requests.entrySet()) {
      forceCreate(v.getKey(), v.getValue());
    }

    var result = client.get()
        .uri("/api/v1/heroes/items")
        .accept(APPLICATION_NDJSON)
        .exchange()
        .expectStatus().isOk()
        .returnResult(Hero.class).getResponseBody();

    StepVerifier.create(result)
        .expectNextCount(3)
        .thenCancel()
        .verify();
  }
}
