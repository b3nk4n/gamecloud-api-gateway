package de.b3nk4n.gamecloud.apigateway.controller;

import de.b3nk4n.gamecloud.apigateway.config.SecurityConfig;
import de.b3nk4n.gamecloud.apigateway.model.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.oidc.StandardClaimNames;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@WebFluxTest
@Import(SecurityConfig.class)
class UserControllerTest {
    @Autowired
    WebTestClient webClient;

    @MockBean
    ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
    void whenNotAuthenticatedThen401() {
        webClient
                .get()
                .uri("/user")
                .exchange()
                .expectStatus()
                .isUnauthorized();
    }

    @Test
    void whenAuthenticatedThenReturnUser() {
        final var expectedUser = new User("b3nk4n", "Benjamin", "Kan", List.of("employee", "customer"));

        webClient
                .mutateWith(configureMockOidcLogin(expectedUser))
                .get()
                .uri("/user")
                .exchange()
                .expectStatus().is2xxSuccessful()
                .expectBody(User.class)
                .value(user -> assertThat(user).isEqualTo(expectedUser));
    }

    private SecurityMockServerConfigurers.OidcLoginMutator configureMockOidcLogin(User user) {
        return SecurityMockServerConfigurers.mockOidcLogin().idToken(
                builder -> builder
                        .claim(StandardClaimNames.PREFERRED_USERNAME, user.username())
                        .claim(StandardClaimNames.GIVEN_NAME, user.firstName())
                        .claim(StandardClaimNames.FAMILY_NAME, user.lastName()));
    }
}
