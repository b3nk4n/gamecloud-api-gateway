package de.b3nk4n.gamecloud.apigateway.config;

import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Mono;

@Configuration
public class RateLimiterConfig {
    @Bean
    public KeyResolver keyResolver() {
        // rate limiting grouped by a constant pseudo key as long as there is no user session/authentication concept
        return exchange -> Mono.just("anonymous");
    }
}
