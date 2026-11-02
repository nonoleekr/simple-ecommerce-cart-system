package io.github.nonoleekr.cartiva.catalog;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.net.URI;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
@Validated
@Tag(name = "Catalog", description = "Product discovery and seller catalog management")
public class CatalogController {
    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/products")
    @Operation(summary = "Browse active products")
    public PageResponse<ProductResponse> browseProducts(
        @RequestParam(required = false) String query,
        @RequestParam(required = false) String category,
        @RequestParam(defaultValue = "newest") String sort,
        @RequestParam(defaultValue = "0") @Min(0) int page,
        @RequestParam(defaultValue = "20") @Min(1) @Max(100) int size
    ) {
        return catalogService.browse(query, category, sort, page, size);
    }

    @GetMapping("/products/{slug}")
    @Operation(summary = "Get an active product")
    public ProductResponse getProduct(@PathVariable String slug) {
        return catalogService.findActiveProduct(slug);
    }

    @GetMapping("/categories")
    @Operation(summary = "List product categories")
    public List<CategoryResponse> listCategories() {
        return catalogService.listCategories();
    }

    @PostMapping("/seller/products")
    @Operation(summary = "Create a draft product")
    public ResponseEntity<ProductResponse> createProduct(
        @AuthenticationPrincipal Jwt jwt,
        @Valid @RequestBody CreateProductRequest request
    ) {
        ProductResponse product = catalogService.createDraft(UUID.fromString(jwt.getSubject()), request);
        return ResponseEntity.created(URI.create("/api/v1/seller/products/" + product.id())).body(product);
    }

    @GetMapping("/seller/products")
    @Operation(summary = "List the current seller's products")
    public List<ProductResponse> listSellerProducts(@AuthenticationPrincipal Jwt jwt) {
        return catalogService.listSellerProducts(UUID.fromString(jwt.getSubject()));
    }

    @PutMapping("/seller/products/{productId}")
    @Operation(summary = "Update a seller-owned product")
    public ProductResponse updateProduct(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID productId, @Valid @RequestBody UpdateProductRequest request) {
        return catalogService.updateSellerProduct(UUID.fromString(jwt.getSubject()), productId, request);
    }

    @PatchMapping("/seller/products/{productId}/status")
    @Operation(summary = "Publish, archive, or return a product to draft")
    public ProductResponse updateProductStatus(@AuthenticationPrincipal Jwt jwt, @PathVariable UUID productId, @Valid @RequestBody UpdateProductStatusRequest request) {
        return catalogService.updateSellerProductStatus(UUID.fromString(jwt.getSubject()), productId, request.status());
    }
}
