package de.b3nk4n.gamecloud.apigateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.test.web.reactive.server.SecurityMockServerConfigurers;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.Mockito.when;

@WebFluxTest
@Import(SecurityConfig.class)
class SecurityConfigTest {
    /**
     * Spring's {@link SecurityMockServerConfigurers#mockOidcLogin()} seems to require to use {@code "test"} when fetching the 
     * registration via {@link ReactiveClientRegistrationRepository#findByRegistrationId(String)}.
     */
    private static final String TEST_REGISTRATION_ID = "test";
    
    @Autowired
    WebTestClient webClient;

    /**
     * A mock bean to skip the interaction with Keycloak when retrieving information about the client registration.
     */
    @MockBean
    ReactiveClientRegistrationRepository clientRegistrationRepository;

    @Test
    void whenLogoutAuthenticatedAndWithCsrfTokenThen302() {
        when(clientRegistrationRepository.findByRegistrationId(TEST_REGISTRATION_ID))
                .thenReturn(Mono.just(testClientRegistration()));

        webClient
                .mutateWith(SecurityMockServerConfigurers.mockOidcLogin())
                .mutateWith(SecurityMockServerConfigurers.csrf())
                .post()
                .uri("/logout")
                .exchange()
                .expectStatus().isFound();
    }

    private ClientRegistration testClientRegistration() {
        return ClientRegistration.withRegistrationId(TEST_REGISTRATION_ID)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .clientId("testClientId")
                .authorizationUri("https://sso.gamecloud.com/auth")
                .tokenUri("https://sso.gamecloud.com/token")
                .redirectUri("https://gamecloud.com")
                .build();
    }
}
