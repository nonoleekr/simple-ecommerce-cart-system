package io.github.nonoleekr.cartiva.review;

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
@Table(name = "product_review", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "customer_id"}))
public class ProductReview {
    @Id private UUID id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "customer_id", nullable = false)
    private User customer;
    @Column(nullable = false) private int rating;
    @Column(nullable = false, length = 120) private String title;
    @Column(nullable = false, length = 2000) private String comment;
    @Column(name = "created_at", nullable = false, updatable = false) private Instant createdAt;

    protected ProductReview() {}

    public ProductReview(Product product, User customer, int rating, String title, String comment) {
        this.id = UUID.randomUUID(); this.product = product; this.customer = customer;
        this.rating = rating; this.title = title; this.comment = comment;
    }

    @PrePersist void onCreate() { createdAt = Instant.now(); }
    public UUID getId() { return id; }
    public User getCustomer() { return customer; }
    public int getRating() { return rating; }
    public String getTitle() { return title; }
    public String getComment() { return comment; }
    public Instant getCreatedAt() { return createdAt; }
}
