package io.github.nonoleekr.cartiva.review;

import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products/{productId}/reviews")
public class ReviewController {
    private final ReviewService reviewService;
    public ReviewController(ReviewService reviewService) { this.reviewService = reviewService; }

    @GetMapping
    public ProductReviewsResponse getReviews(@PathVariable UUID productId) { return reviewService.getReviews(productId); }

    @PostMapping
    public ResponseEntity<ReviewResponse> create(@PathVariable UUID productId, @AuthenticationPrincipal Jwt jwt, @Valid @RequestBody CreateReviewRequest request) {
        ReviewResponse review = reviewService.create(productId, UUID.fromString(jwt.getSubject()), request);
        return ResponseEntity.created(URI.create("/api/v1/products/" + productId + "/reviews/" + review.id())).body(review);
    }
}
