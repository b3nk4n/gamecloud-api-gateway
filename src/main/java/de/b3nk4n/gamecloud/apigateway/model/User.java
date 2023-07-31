package de.b3nk4n.gamecloud.apigateway.model;

import java.util.List;

public record User(
        String username,
        String firstName,
        String lastName,
        List<String> roles
) {
}
