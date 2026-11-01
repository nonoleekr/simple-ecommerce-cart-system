package io.github.nonoleekr.cartiva.identity;

import java.time.Instant;

public record AuthenticationResponse(
    String accessToken,
    String tokenType,
    Instant expiresAt,
    UserResponse user
) {
}
