package io.github.nonoleekr.cartiva.review;

import java.time.Instant;
import java.util.UUID;

public record ReviewResponse(UUID id, int rating, String title, String comment, String customerName, Instant createdAt) {
    static ReviewResponse from(ProductReview review) {
        return new ReviewResponse(review.getId(), review.getRating(), review.getTitle(), review.getComment(), review.getCustomer().getDisplayName(), review.getCreatedAt());
    }
}
