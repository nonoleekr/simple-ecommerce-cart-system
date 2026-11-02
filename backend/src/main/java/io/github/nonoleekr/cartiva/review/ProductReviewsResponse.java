package io.github.nonoleekr.cartiva.review;

import java.util.List;

public record ProductReviewsResponse(double averageRating, int reviewCount, List<ReviewResponse> reviews) {}
