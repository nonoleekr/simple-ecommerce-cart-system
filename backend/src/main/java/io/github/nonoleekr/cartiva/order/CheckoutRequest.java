package io.github.nonoleekr.cartiva.order;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CheckoutRequest(
    @NotBlank @Size(max = 100) String recipientName,
    @NotBlank @Size(max = 200) String addressLine1,
    @NotBlank @Size(max = 100) String city,
    @NotBlank @Size(max = 20) String postalCode,
    @NotBlank @Size(max = 100) String country
) {
}
