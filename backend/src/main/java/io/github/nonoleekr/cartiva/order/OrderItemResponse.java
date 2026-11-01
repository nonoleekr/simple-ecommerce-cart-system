package io.github.nonoleekr.cartiva.order;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemResponse(
    UUID productId,
    String productName,
    BigDecimal unitPrice,
    int quantity,
    BigDecimal lineTotal
) {
    static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
            item.getProductId(),
            item.getProductName(),
            item.getUnitPrice(),
            item.getQuantity(),
            item.getLineTotal()
        );
    }
}
