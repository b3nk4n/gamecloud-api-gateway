# GameCloud API Gateway

This application is part of the GameCloud system and provides the API gateway and handling of cross-cutting concerns.
The project was created based on a similar project of the
[Cloud Native Spring in Action](https://www.manning.com/books/cloud-native-spring-in-action) book
by [Thomas Vitale](https://www.thomasvitale.com).

## Useful Commands

| Gradle Command	         | Description                                                    |
|:---------------------------|:---------------------------------------------------------------|
| `./gradlew bootRun`        | Run the application.                                           |
| `./gradlew build`          | Build the application.                                         |
| `./gradlew test`           | Run tests.                                                     |
| `./gradlew bootJar`        | Package the application as a JAR.                              |
| `./gradlew bootBuildImage` | Package the application as a container image using Buildpacks. |

## Local development and tools

Check out the rather similar command for GameCloud [catalog-service](https://github.com/b3nk4n/gamecloud-catalog-service#local-development-and-tools).

### Testing the Circuit Breaker

The Apache HTTP benchmark took `ab` can be used to test the circuit breaker.

```bash
ab -n 21 -c 1 -m POST http://localhost:9000/orders
ab -n 21 -c 1 -m GET http://localhost:9000/games
```
