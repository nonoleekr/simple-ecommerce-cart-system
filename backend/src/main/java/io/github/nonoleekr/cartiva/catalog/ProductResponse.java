package io.github.nonoleekr.cartiva.catalog;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ProductResponse(
    UUID id,
    String name,
    String slug,
    String description,
    BigDecimal price,
    int stockQuantity,
    ProductStatus status,
    CategoryResponse category,
    String sellerName,
    Instant createdAt
) {
    public static ProductResponse from(Product product) {
        return new ProductResponse(
            product.getId(),
            product.getName(),
            product.getSlug(),
            product.getDescription(),
            product.getPrice(),
            product.getStockQuantity(),
            product.getStatus(),
            CategoryResponse.from(product.getCategory()),
            product.getSeller().getDisplayName(),
            product.getCreatedAt()
        );
    }
}
