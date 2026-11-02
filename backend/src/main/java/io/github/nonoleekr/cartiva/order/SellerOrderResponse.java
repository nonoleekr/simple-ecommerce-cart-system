package io.github.nonoleekr.cartiva.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record SellerOrderResponse(UUID orderId, OrderStatus status, BigDecimal sellerTotal, String recipientName, String city, String country, List<OrderItemResponse> items, Instant createdAt) {
    static SellerOrderResponse from(SellerFulfillment fulfillment) {
        Order order = fulfillment.getOrder();
        UUID sellerId = fulfillment.getSeller().getId();
        List<OrderItemResponse> items = order.getItems().stream()
            .filter(item -> item.getSellerId().equals(sellerId))
            .map(OrderItemResponse::from)
            .toList();
        BigDecimal total = items.stream().map(OrderItemResponse::lineTotal).reduce(BigDecimal.ZERO, BigDecimal::add);
        return new SellerOrderResponse(order.getId(), fulfillment.getStatus(), total, order.getRecipientName(), order.getCity(), order.getCountry(), items, order.getCreatedAt());
    }
}
