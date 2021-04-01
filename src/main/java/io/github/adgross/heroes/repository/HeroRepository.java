package io.github.adgross.heroes.repository;

import io.github.adgross.heroes.model.Hero;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbAsyncClient;
import software.amazon.awssdk.services.dynamodb.model.DeleteTableRequest;

@Repository
@Slf4j
public class HeroRepository {
  private final DynamoDbAsyncClient ddb;
  private final DynamoDbEnhancedAsyncClient enhancedClient;
  private final DynamoDbAsyncTable<Hero> heroTable;
  private final String tableName;

  public HeroRepository(@Value("${dynamodb.region}") String region,
                        @Value("${dynamodb.endpoint}") String endpoint,
                        @Value("${dynamodb.table}") String table) {
    var dynamodbRegion = Region.of(region);
    tableName = table;
    ddb = DynamoDbAsyncClient.builder()
        .region(dynamodbRegion)
        .endpointOverride(URI.create(endpoint))
        .build();
    enhancedClient = DynamoDbEnhancedAsyncClient.builder()
        .dynamoDbClient(ddb)
        .build();
    heroTable = enhancedClient.table(
        table,
        TableSchema.fromBean(Hero.class));
    Mono.fromFuture(heroTable.createTable())
        .subscribe(
            ok -> log.info("Table created: " + table),
            fail -> log.info("Looks we already have the table " + table)
        );
  }

  public Mono<Hero> get(String id) {
    var key = Key.builder()
        .partitionValue(id)
        .build();
    var result = heroTable.getItem(key);
    return Mono.fromFuture(result);
  }

  public Flux<Hero> getAll() {
    var results = heroTable.scan().items();
    return Flux.from(results).onBackpressureBuffer();
  }

  public Mono<Void> put(Hero hero) {
    return Mono.fromFuture(heroTable.putItem(hero));
  }

  public Mono<Hero> update(Hero hero) {
    return Mono.fromFuture(heroTable.updateItem((hero)));
  }

  public Mono<Hero> delete(Hero hero) {
    return Mono.fromFuture(heroTable.deleteItem(hero));
  }

  public Mono<Hero> delete(String id) {
    var key = Key.builder()
        .partitionValue(id)
        .build();
    return Mono.fromFuture(heroTable.deleteItem(key));
  }

  public Mono<Void> resetTable() {
    var delRequest = DeleteTableRequest.builder().tableName(tableName).build();
    var delete = Mono.fromFuture(ddb.deleteTable(delRequest));
    var create = Mono.fromFuture(heroTable.createTable());

    return delete.then(create);
  }

}
