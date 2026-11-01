package io.github.nonoleekr.cartiva.order;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, UUID> {
    @EntityGraph(attributePaths = {"items", "items.product"})
    List<Order> findAllByCustomerIdOrderByCreatedAtDesc(UUID customerId);
}
