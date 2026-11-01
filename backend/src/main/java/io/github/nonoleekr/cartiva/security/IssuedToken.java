package io.github.nonoleekr.cartiva.security;

import java.time.Instant;

public record IssuedToken(String value, Instant expiresAt) {
}
