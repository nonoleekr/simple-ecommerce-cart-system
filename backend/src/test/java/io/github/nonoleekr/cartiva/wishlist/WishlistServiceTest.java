package io.github.nonoleekr.cartiva.wishlist;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.nonoleekr.cartiva.catalog.Category;
import io.github.nonoleekr.cartiva.catalog.Product;
import io.github.nonoleekr.cartiva.catalog.ProductRepository;
import io.github.nonoleekr.cartiva.catalog.ProductStatus;
import io.github.nonoleekr.cartiva.identity.Role;
import io.github.nonoleekr.cartiva.identity.User;
import io.github.nonoleekr.cartiva.identity.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WishlistServiceTest {
    @Mock WishlistRepository wishlistRepository;
    @Mock ProductRepository productRepository;
    @Mock UserRepository userRepository;
    private WishlistService wishlistService;

    @BeforeEach
    void setUp() {
        wishlistService = new WishlistService(wishlistRepository, productRepository, userRepository);
    }

    @Test
    void addsAnAvailableProductOnlyOnce() {
        UUID customerId = UUID.randomUUID();
        User customer = user("customer");
        Product product = product();
        when(wishlistRepository.findByCustomerIdAndProductId(customerId, product.getId()))
            .thenReturn(Optional.empty(), Optional.of(new WishlistItem(customer, product)));
        when(userRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(productRepository.findByIdAndStatus(product.getId(), ProductStatus.ACTIVE)).thenReturn(Optional.of(product));

        wishlistService.add(customerId, product.getId());
        wishlistService.add(customerId, product.getId());

        verify(wishlistRepository).save(any(WishlistItem.class));
    }

    @Test
    void excludesUnavailableProductsFromResults() {
        UUID customerId = UUID.randomUUID();
        User customer = user("customer");
        Product active = product();
        Product draft = new Product(user("seller-two"), mock(Category.class), "Draft", "draft", "Draft", BigDecimal.TEN, 2, ProductStatus.DRAFT);
        when(wishlistRepository.findAllByCustomerIdOrderByCreatedAtDesc(customerId))
            .thenReturn(List.of(new WishlistItem(customer, draft), new WishlistItem(customer, active)));

        assertThat(wishlistService.getWishlist(customerId)).singleElement()
            .extracting(response -> response.id())
            .isEqualTo(active.getId());
    }

    @Test
    void rejectsUnavailableProducts() {
        UUID customerId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        when(wishlistRepository.findByCustomerIdAndProductId(customerId, productId)).thenReturn(Optional.empty());
        when(userRepository.findById(customerId)).thenReturn(Optional.of(user("customer")));
        when(productRepository.findByIdAndStatus(productId, ProductStatus.ACTIVE)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> wishlistService.add(customerId, productId))
            .isInstanceOf(WishlistOperationException.class)
            .hasMessage("Product is not available");
        verify(wishlistRepository, never()).save(any());
    }

    private User user(String username) {
        return new User(username, username + "@example.com", "hash", username, mock(Role.class));
    }

    private Product product() {
        return new Product(user("seller"), mock(Category.class), "Keyboard", "keyboard", "Description", BigDecimal.TEN, 2, ProductStatus.ACTIVE);
    }
}
