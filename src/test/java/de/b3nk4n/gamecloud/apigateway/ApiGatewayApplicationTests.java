package de.b3nk4n.gamecloud.apigateway;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers // activates automatic startup and cleanup of test containers
class ApiGatewayApplicationTests {
    private static final int redisPort = 6379;

    /**
     * A mock bean to skip the interaction with Keycloak when retrieving information about the client registration,
     * which would otherwise fail the test as keycloak is not available.
     */
    @MockBean
    ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Container
    static GenericContainer<?> redis = new GenericContainer<>(DockerImageName.parse("redis:7.0.12"))
            .withExposedPorts(redisPort);

    @DynamicPropertySource
    static void redisProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.redis.host", () -> redis.getHost());
        registry.add("spring.redis.port", () -> redis.getMappedPort(redisPort));
    }

    @Test
    void verifyThatSpringContextLoads() {
        // no assert needed
    }

}
