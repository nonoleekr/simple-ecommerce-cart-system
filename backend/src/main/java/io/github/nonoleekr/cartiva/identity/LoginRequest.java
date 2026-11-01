package io.github.nonoleekr.cartiva.identity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
    @NotBlank @Size(max = 254) String identifier,
    @NotBlank @Size(max = 72) String password
) {
}
