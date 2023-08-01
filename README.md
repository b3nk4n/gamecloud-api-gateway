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

### External access

To have external access to the cluster without the need for `kubectl port-forward`,
we can use [Ingress Nginx](https://github.com/kubernetes/ingress-nginx). When using Minikube, we need to enable the _ingress_
add-on first:

```bash
minikube start --cpus 2 --memory 4g --driver docker --profile gamecloud
minikube addons enable ingress --profile gamecloud
minikube tunnel --profile gamecloud
```

Alternatively, at least on Linux, you can use the IP address of the cluster instead of using `minikube tunnel`:

```bash
minikube ip --profile gamecloud
```

This is similar to the `kubectl port-forward` command, but it applies to the whole cluster instead of a single service.

## Authorization

The authorization flow can be started from a single page application (SPA) via the `/oauth2/authorization/{registrationId}`
endpoint, such as via `window.open('/oauth2/authorization/keycloak', '_self')`.
