package io.github.nonoleekr.cartiva.order;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/seller/orders")
public class SellerOrderController {
    private final SellerOrderService sellerOrderService;
    public SellerOrderController(SellerOrderService sellerOrderService) { this.sellerOrderService = sellerOrderService; }

    @GetMapping
    public List<SellerOrderResponse> getOrders(@AuthenticationPrincipal Jwt jwt) {
        return sellerOrderService.getOrders(UUID.fromString(jwt.getSubject()));
    }

    @PatchMapping("/{orderId}/status")
    public SellerOrderResponse updateStatus(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID orderId, @Valid @RequestBody UpdateOrderStatusRequest request) {
        return sellerOrderService.updateStatus(UUID.fromString(jwt.getSubject()), orderId, request.status());
    }
}
