package io.github.nonoleekr.cartiva.wishlist;

import io.github.nonoleekr.cartiva.catalog.Product;
import io.github.nonoleekr.cartiva.catalog.ProductRepository;
import io.github.nonoleekr.cartiva.catalog.ProductResponse;
import io.github.nonoleekr.cartiva.catalog.ProductStatus;
import io.github.nonoleekr.cartiva.identity.User;
import io.github.nonoleekr.cartiva.identity.UserRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class WishlistService {
    private final WishlistRepository wishlistRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public WishlistService(WishlistRepository wishlistRepository, ProductRepository productRepository, UserRepository userRepository) {
        this.wishlistRepository = wishlistRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getWishlist(UUID customerId) {
        return wishlistRepository.findAllByCustomerIdOrderByCreatedAtDesc(customerId).stream()
            .filter(item -> item.getProduct().getStatus() == ProductStatus.ACTIVE)
            .map(WishlistItem::getProduct)
            .map(ProductResponse::from)
            .toList();
    }

    @Transactional
    public void add(UUID customerId, UUID productId) {
        if (wishlistRepository.findByCustomerIdAndProductId(customerId, productId).isPresent()) return;
        User customer = userRepository.findById(customerId)
            .orElseThrow(() -> new WishlistOperationException("Customer account was not found"));
        Product product = productRepository.findByIdAndStatus(productId, ProductStatus.ACTIVE)
            .orElseThrow(() -> new WishlistOperationException("Product is not available"));
        wishlistRepository.save(new WishlistItem(customer, product));
    }

    @Transactional
    public void remove(UUID customerId, UUID productId) {
        wishlistRepository.findByCustomerIdAndProductId(customerId, productId)
            .ifPresent(wishlistRepository::delete);
    }
}
