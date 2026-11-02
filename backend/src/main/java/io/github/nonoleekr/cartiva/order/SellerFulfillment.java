package io.github.nonoleekr.cartiva.order;

import io.github.nonoleekr.cartiva.identity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "seller_fulfillment")
public class SellerFulfillment {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "seller_id", nullable = false)
    private User seller;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OrderStatus status;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    protected SellerFulfillment() {}
    public SellerFulfillment(Order order, User seller) { this.id = UUID.randomUUID(); this.order = order; this.seller = seller; this.status = OrderStatus.CONFIRMED; }
    @PrePersist @PreUpdate void touch() { updatedAt = Instant.now(); }
    public Order getOrder() { return order; }
    public User getSeller() { return seller; }
    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }
}
