package io.github.nonoleekr.cartiva.wishlist;

import io.github.nonoleekr.cartiva.catalog.Product;
import io.github.nonoleekr.cartiva.identity.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "wishlist_item", uniqueConstraints = @UniqueConstraint(columnNames = {"customer_id", "product_id"}))
public class WishlistItem {
    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    protected WishlistItem() {}

    public WishlistItem(User customer, Product product) {
        this.id = UUID.randomUUID();
        this.customer = customer;
        this.product = product;
    }

    @PrePersist
    void onCreate() { createdAt = Instant.now(); }

    public Product getProduct() { return product; }
}
