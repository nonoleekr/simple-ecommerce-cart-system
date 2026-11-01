package io.github.nonoleekr.cartiva.cart;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cart")
@Tag(name = "Cart", description = "Authenticated customer shopping cart")
public class CartController {
    private final CartService cartService;

    public CartController(CartService cartService) {
        this.cartService = cartService;
    }

    @GetMapping
    @Operation(summary = "Get the current customer's cart")
    public CartResponse getCart(@AuthenticationPrincipal Jwt jwt) {
        return cartService.getCart(customerId(jwt));
    }

    @PostMapping("/items")
    @Operation(summary = "Add a product to the cart")
    public CartResponse addItem(
        @AuthenticationPrincipal Jwt jwt,
        @Valid @RequestBody AddCartItemRequest request
    ) {
        return cartService.addItem(customerId(jwt), request);
    }

    @PatchMapping("/items/{productId}")
    @Operation(summary = "Set a cart item's quantity")
    public CartResponse updateItem(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID productId,
        @Valid @RequestBody UpdateCartItemRequest request
    ) {
        return cartService.updateItem(customerId(jwt), productId, request.quantity());
    }

    @DeleteMapping("/items/{productId}")
    @Operation(summary = "Remove a product from the cart")
    public CartResponse removeItem(
        @AuthenticationPrincipal Jwt jwt,
        @PathVariable UUID productId
    ) {
        return cartService.removeItem(customerId(jwt), productId);
    }

    private UUID customerId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
