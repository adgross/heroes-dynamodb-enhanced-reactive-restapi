## Heroes REST API

This project aim to create a simple back-end REST server using the new DynamoDB enhanced client (AWS SDK v2).

### Dependencies

- Targets Java 11.
- Build with Gradle.
- Need a DynamoDB connection, or a DynamoDB-local connection

### Running a local DynamoDB

Using docker or podman, execute:

        podman run -p 8000:8000 amazon/dynamodb-local

### Build and run

The following instructions are expected to be run in the project root directory.

* Generate a gradle wrapper. We don't include a gradle wrapper. If you want to build with your system installed gradle, skip this and use `gradle` instead of `./gradlew` in next steps.

        gradle wrapper

* Build the project. This also run tests, integration tests are going to fail if we don't have an active DynamoDB connection.

        ./gradlew build

* _Alternative:_ Build the project without tests.

        ./gradlew assemble

* Run the built jar.

        java -jar build/libs/heroes-dynamodb-enhanced-reactive-restapi-1.0.0-SNAPSHOT.jar

* Run the server using gradle (default port is 8080).

        ./gradlew bootrun

### API
| Method | URI                   | Payload   | Description                         |
| :---   | :---------------      | :----     | :---------------------------        |
| GET    | /api/v1/heroes        | < empty > | Request all heroes in one json      |
| GET    | /api/v1/heroes/items  | < empty > | Request a continuous stream of data |
| GET    | /api/v1/heroes/{uuid} | < empty > | Return a hero by the given id       |
| DELETE | /api/v1/heroes/{uuid} | < empty > | Same as GET, but delete the hero    |
| POST   | /api/v1/heroes        | Hero JSON | Create a hero with random uuid      |
| POST   | /api/v1/heroes/{uuid} | Hero JSON | Create a hero with given uuid       |
| PUT    | /api/v1/heroes/{uuid} | Hero JSON | Replace a hero                      |

### Default settings
- default settings are in `application.yml`
- to change defaults you can use command line arguments, example:
  `-Dserver.port=8083` or `--server.port=8083`
- you might want to create an `application-local.yml` to override settings
- the default DynamoDB endpoint is `http://localhost:8000`