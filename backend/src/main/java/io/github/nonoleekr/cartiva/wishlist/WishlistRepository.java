package io.github.nonoleekr.cartiva.wishlist;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WishlistRepository extends JpaRepository<WishlistItem, UUID> {
    @EntityGraph(attributePaths = {"product", "product.category", "product.seller"})
    List<WishlistItem> findAllByCustomerIdOrderByCreatedAtDesc(UUID customerId);

    Optional<WishlistItem> findByCustomerIdAndProductId(UUID customerId, UUID productId);
}
