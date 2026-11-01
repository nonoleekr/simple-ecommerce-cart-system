package io.github.nonoleekr.cartiva.cart;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record AddCartItemRequest(
    @NotNull UUID productId,
    @Min(1) int quantity
) {
}
