package io.github.nonoleekr.cartiva.catalog;

import jakarta.validation.constraints.NotNull;

public record UpdateProductStatusRequest(@NotNull ProductStatus status) {}
