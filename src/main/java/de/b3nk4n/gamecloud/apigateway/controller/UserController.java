package de.b3nk4n.gamecloud.apigateway.controller;

import de.b3nk4n.gamecloud.apigateway.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
public class UserController {
    @GetMapping("user-from-context")
    public Mono<User> getUserFromContext() {
        return ReactiveSecurityContextHolder.getContext()
                .map(SecurityContext::getAuthentication)
                .map(Authentication::getPrincipal)
                .map(OidcUser.class::cast)
                .map(UserController::fromOidcUser);
    }

    @GetMapping("user")
    public Mono<User> getUserViaInjection(@AuthenticationPrincipal OidcUser oidcUser) {
        return Mono.just(fromOidcUser(oidcUser));
    }

    private static User fromOidcUser(OidcUser oidcUser) {
        return new User(
                oidcUser.getPreferredUsername(),
                oidcUser.getGivenName(),
                oidcUser.getFamilyName(),
                oidcUser.getClaimAsStringList("roles")
        );
    }
}
