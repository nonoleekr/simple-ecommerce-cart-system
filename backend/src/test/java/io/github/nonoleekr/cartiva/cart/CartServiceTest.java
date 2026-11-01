package io.github.nonoleekr.cartiva.cart;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.nonoleekr.cartiva.catalog.Category;
import io.github.nonoleekr.cartiva.catalog.Product;
import io.github.nonoleekr.cartiva.catalog.ProductRepository;
import io.github.nonoleekr.cartiva.catalog.ProductStatus;
import io.github.nonoleekr.cartiva.identity.Role;
import io.github.nonoleekr.cartiva.identity.User;
import io.github.nonoleekr.cartiva.identity.UserRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {
    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        cartService = new CartService(cartRepository, productRepository, userRepository);
    }

    @Test
    void addsProductsAndAccumulatesServerCalculatedTotals() {
        UUID customerId = UUID.randomUUID();
        User customer = user("customer", "Customer");
        Product product = product("Mechanical Keyboard", new BigDecimal("249.90"), 10);
        Cart cart = new Cart(customer);
        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(productRepository.findByIdAndStatus(product.getId(), ProductStatus.ACTIVE))
            .thenReturn(Optional.of(product));

        cartService.addItem(customerId, new AddCartItemRequest(product.getId(), 2));
        CartResponse response = cartService.addItem(
            customerId,
            new AddCartItemRequest(product.getId(), 1)
        );

        assertThat(response.totalQuantity()).isEqualTo(3);
        assertThat(response.subtotal()).isEqualByComparingTo("749.70");
        assertThat(response.items()).singleElement()
            .extracting(CartItemResponse::quantity)
            .isEqualTo(3);
    }

    @Test
    void rejectsQuantityAboveAvailableStock() {
        UUID customerId = UUID.randomUUID();
        User customer = user("customer", "Customer");
        Product product = product("Limited Item", new BigDecimal("10.00"), 2);
        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(new Cart(customer)));
        when(productRepository.findByIdAndStatus(product.getId(), ProductStatus.ACTIVE))
            .thenReturn(Optional.of(product));

        assertThatThrownBy(() -> cartService.addItem(
            customerId,
            new AddCartItemRequest(product.getId(), 3)
        ))
            .isInstanceOf(CartOperationException.class)
            .hasMessage("Requested quantity exceeds available stock");
    }

    private User user(String username, String displayName) {
        return new User(
            username,
            username + "@example.com",
            "hash",
            displayName,
            mock(Role.class)
        );
    }

    private Product product(String name, BigDecimal price, int stock) {
        return new Product(
            user("seller", "Seller"),
            mock(Category.class),
            name,
            name.toLowerCase().replace(' ', '-'),
            "Description",
            price,
            stock,
            ProductStatus.ACTIVE
        );
    }
}
