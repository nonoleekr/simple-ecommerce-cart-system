package io.github.nonoleekr.cartiva.review;

import io.github.nonoleekr.cartiva.catalog.Product;
import io.github.nonoleekr.cartiva.catalog.ProductRepository;
import io.github.nonoleekr.cartiva.catalog.ProductStatus;
import io.github.nonoleekr.cartiva.identity.User;
import io.github.nonoleekr.cartiva.identity.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ReviewService {
    private final ProductReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public ReviewService(ProductReviewRepository reviewRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.reviewRepository = reviewRepository; this.productRepository = productRepository; this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public ProductReviewsResponse getReviews(UUID productId) {
        requireActiveProduct(productId);
        List<ReviewResponse> reviews = reviewRepository.findAllByProductIdOrderByCreatedAtDesc(productId).stream().map(ReviewResponse::from).toList();
        double average = reviews.stream().mapToInt(ReviewResponse::rating).average().orElse(0);
        return new ProductReviewsResponse(Math.round(average * 10.0) / 10.0, reviews.size(), reviews);
    }

    @Transactional
    public ReviewResponse create(UUID productId, UUID customerId, CreateReviewRequest request) {
        if (reviewRepository.existsByProductIdAndCustomerId(productId, customerId)) {
            throw new ReviewOperationException("You have already reviewed this product");
        }
        Product product = requireActiveProduct(productId);
        User customer = userRepository.findById(customerId).orElseThrow(() -> new ReviewOperationException("Customer account was not found"));
        return ReviewResponse.from(reviewRepository.save(new ProductReview(product, customer, request.rating(), request.title().trim(), request.comment().trim())));
    }

    private Product requireActiveProduct(UUID productId) {
        return productRepository.findByIdAndStatus(productId, ProductStatus.ACTIVE)
            .orElseThrow(() -> new ReviewOperationException("Product is not available"));
    }
}
