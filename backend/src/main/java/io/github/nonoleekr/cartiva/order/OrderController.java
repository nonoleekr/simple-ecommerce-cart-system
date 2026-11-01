package io.github.nonoleekr.cartiva.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@Tag(name = "Orders", description = "Simulated checkout and customer order history")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping("/checkout")
    @Operation(summary = "Checkout the current cart")
    public ResponseEntity<OrderResponse> checkout(
        @AuthenticationPrincipal Jwt jwt,
        @Valid @RequestBody CheckoutRequest request
    ) {
        OrderResponse order = orderService.checkout(customerId(jwt), request);
        return ResponseEntity.created(URI.create("/api/v1/orders/" + order.id())).body(order);
    }

    @GetMapping
    @Operation(summary = "Get the current customer's order history")
    public List<OrderResponse> getOrderHistory(@AuthenticationPrincipal Jwt jwt) {
        return orderService.getOrderHistory(customerId(jwt));
    }

    private UUID customerId(Jwt jwt) {
        return UUID.fromString(jwt.getSubject());
    }
}
