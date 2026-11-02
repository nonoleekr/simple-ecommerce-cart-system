package io.github.nonoleekr.cartiva.review;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductReviewRepository extends JpaRepository<ProductReview, UUID> {
    boolean existsByProductIdAndCustomerId(UUID productId, UUID customerId);

    @EntityGraph(attributePaths = "customer")
    List<ProductReview> findAllByProductIdOrderByCreatedAtDesc(UUID productId);
}
