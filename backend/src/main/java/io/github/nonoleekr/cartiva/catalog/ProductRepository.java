package io.github.nonoleekr.cartiva.catalog;

import java.util.Optional;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;

public interface ProductRepository extends JpaRepository<Product, UUID> {
    boolean existsBySlug(String slug);
    boolean existsBySlugAndIdNot(String slug, UUID id);

    @EntityGraph(attributePaths = {"category", "seller"})
    List<Product> findAllBySellerIdOrderByCreatedAtDesc(UUID sellerId);

    @EntityGraph(attributePaths = {"category", "seller"})
    Optional<Product> findByIdAndSellerId(UUID id, UUID sellerId);

    @EntityGraph(attributePaths = {"category", "seller"})
    Optional<Product> findBySlugAndStatus(String slug, ProductStatus status);

    @EntityGraph(attributePaths = {"category", "seller"})
    Optional<Product> findByIdAndStatus(UUID id, ProductStatus status);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @EntityGraph(attributePaths = {"category", "seller"})
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") UUID id);

    @EntityGraph(attributePaths = {"category", "seller"})
    @Query("""
        SELECT p FROM Product p
        WHERE p.status = :status
          AND (:query IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
               OR LOWER(p.description) LIKE LOWER(CONCAT('%', :query, '%')))
          AND (:categorySlug IS NULL OR p.category.slug = :categorySlug)
        """)
    Page<Product> searchCatalog(
        @Param("status") ProductStatus status,
        @Param("query") String query,
        @Param("categorySlug") String categorySlug,
        Pageable pageable
    );
}
