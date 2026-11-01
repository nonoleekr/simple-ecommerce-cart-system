package io.github.nonoleekr.cartiva.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
    UUID id,
    OrderStatus status,
    BigDecimal totalAmount,
    String recipientName,
    String addressLine1,
    String city,
    String postalCode,
    String country,
    List<OrderItemResponse> items,
    Instant createdAt
) {
    static OrderResponse from(Order order) {
        return new OrderResponse(
            order.getId(),
            order.getStatus(),
            order.getTotalAmount(),
            order.getRecipientName(),
            order.getAddressLine1(),
            order.getCity(),
            order.getPostalCode(),
            order.getCountry(),
            order.getItems().stream().map(OrderItemResponse::from).toList(),
            order.getCreatedAt()
        );
    }
}
