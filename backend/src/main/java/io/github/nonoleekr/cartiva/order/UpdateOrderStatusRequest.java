package io.github.nonoleekr.cartiva.order;

import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(@NotNull OrderStatus status) {}
