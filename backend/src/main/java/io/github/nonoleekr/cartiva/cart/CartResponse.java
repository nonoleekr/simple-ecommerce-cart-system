package io.github.nonoleekr.cartiva.cart;

import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

public record CartResponse(
    UUID id,
    List<CartItemResponse> items,
    int totalQuantity,
    BigDecimal subtotal
) {
    static CartResponse from(Cart cart) {
        List<CartItemResponse> items = cart.getItems().stream()
            .map(CartItemResponse::from)
            .sorted(Comparator.comparing(CartItemResponse::productName))
            .toList();
        int totalQuantity = items.stream().mapToInt(CartItemResponse::quantity).sum();
        BigDecimal subtotal = items.stream()
            .map(CartItemResponse::subtotal)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CartResponse(cart.getId(), items, totalQuantity, subtotal);
    }
}
