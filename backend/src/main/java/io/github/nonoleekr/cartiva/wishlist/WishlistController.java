package io.github.nonoleekr.cartiva.wishlist;

import io.github.nonoleekr.cartiva.catalog.ProductResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/wishlist")
@Tag(name = "Wishlist", description = "Authenticated customer saved products")
public class WishlistController {
    private final WishlistService wishlistService;

    public WishlistController(WishlistService wishlistService) { this.wishlistService = wishlistService; }

    @GetMapping
    @Operation(summary = "Get the current customer's wishlist")
    public List<ProductResponse> getWishlist(@AuthenticationPrincipal Jwt jwt) {
        return wishlistService.getWishlist(customerId(jwt));
    }

    @PutMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Save a product")
    public void add(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID productId) {
        wishlistService.add(customerId(jwt), productId);
    }

    @DeleteMapping("/items/{productId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove a saved product")
    public void remove(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID productId) {
        wishlistService.remove(customerId(jwt), productId);
    }

    private UUID customerId(Jwt jwt) { return UUID.fromString(jwt.getSubject()); }
}
