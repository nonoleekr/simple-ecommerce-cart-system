package io.github.nonoleekr.cartiva.cart;

import io.github.nonoleekr.cartiva.catalog.Product;
import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(
    UUID productId,
    String productName,
    String productSlug,
    BigDecimal unitPrice,
    int quantity,
    BigDecimal subtotal,
    int availableStock
) {
    static CartItemResponse from(CartItem item) {
        Product product = item.getProduct();
        return new CartItemResponse(
            product.getId(),
            product.getName(),
            product.getSlug(),
            product.getPrice(),
            item.getQuantity(),
            product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())),
            product.getStockQuantity()
        );
    }
}
