package io.github.nonoleekr.cartiva.order;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SellerFulfillmentRepository extends JpaRepository<SellerFulfillment, UUID> {
    @EntityGraph(attributePaths = {"order", "order.items", "order.items.product", "order.items.product.seller", "seller"})
    List<SellerFulfillment> findAllBySellerIdOrderByUpdatedAtDesc(UUID sellerId);

    @EntityGraph(attributePaths = {"order", "order.items", "order.items.product", "order.items.product.seller", "seller"})
    Optional<SellerFulfillment> findByOrderIdAndSellerId(UUID orderId, UUID sellerId);
}
