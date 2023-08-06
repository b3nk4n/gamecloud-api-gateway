package de.b3nk4n.gamecloud.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.client.oidc.web.server.logout.OidcClientInitiatedServerLogoutSuccessHandler;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizedClientRepository;
import org.springframework.security.oauth2.client.web.server.WebSessionServerOAuth2AuthorizedClientRepository;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.csrf.CookieServerCsrfTokenRepository;
import org.springframework.security.web.server.csrf.CsrfToken;
import org.springframework.web.server.WebFilter;
import reactor.core.publisher.Mono;

@Configuration // required when using @EnableWebFluxSecurity since Spring Security 6.0 (https://github.com/spring-projects/spring-security/issues/12434)
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity,
                                                  @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection") ReactiveClientRegistrationRepository clientRegistrationRepository) {
        return httpSecurity
                .authorizeExchange(exchange -> exchange
                        .pathMatchers("/", "*.js", "*.css", "favicon.ico").permitAll()
                        .pathMatchers(HttpMethod.GET, "/games/**").permitAll()
                        .anyExchange().authenticated())
                // disabled the following exception handling as long as we are not using an SPA UI
//                .exceptionHandling(exceptionHandlingSpec ->
//                        // Change default of 302 forwarding to 401 when SPA is used, so that the UI is in control of the
//                        // authentication flow. This means that the UI is presented to unauthenticated users, instead of being
//                        // automatically redirected to the Keycloak login page when not signed in.
//                        exceptionHandlingSpec.authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED)))
                //.formLogin(Customizer.withDefaults())
                .oauth2Login(Customizer.withDefaults())
                .logout(logoutSpec -> {
                    final var oidcLogoutSuccessHandler =
                            new OidcClientInitiatedServerLogoutSuccessHandler(clientRegistrationRepository);
                    oidcLogoutSuccessHandler.setPostLogoutRedirectUri("{baseUri}");
                    logoutSpec.logoutSuccessHandler(oidcLogoutSuccessHandler);
                })
                // required by e.g. an Angular frontend that the CSRF token is part of the HTTP cookie instead of the HTTP header
                .csrf(csrfSpec -> csrfSpec.csrfTokenRepository(CookieServerCsrfTokenRepository.withHttpOnlyFalse()))
                .build();
    }

    @Bean
    WebFilter csrfWebFilter() {
        // Workaround required as described/discussed in https://github.com/spring-projects/spring-security/issues/5766 to ensure
        // subscription to ensure that CookieServerCsrfTokenRepository subscribes to CsrfToken
        return ((exchange, chain) -> {
            exchange.getResponse().beforeCommit(() -> Mono.defer(() -> {
                Mono<CsrfToken> csrfToken =
                        exchange.getAttribute(CsrfToken.class.getName());
                return csrfToken != null ? csrfToken.then() : Mono.empty();
            }));
            return chain.filter(exchange);
        });
    }

    /**
     * Store Access Token in the web session (using Redis due to {@code spring.session.store-type: redis}).
     */
    @Bean
    ServerOAuth2AuthorizedClientRepository authorizedClientRepository() {
        return new WebSessionServerOAuth2AuthorizedClientRepository();
    }
}
