package io.github.nonoleekr.cartiva.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.nonoleekr.cartiva.cart.Cart;
import io.github.nonoleekr.cartiva.cart.CartItem;
import io.github.nonoleekr.cartiva.cart.CartRepository;
import io.github.nonoleekr.cartiva.catalog.Category;
import io.github.nonoleekr.cartiva.catalog.Product;
import io.github.nonoleekr.cartiva.catalog.ProductRepository;
import io.github.nonoleekr.cartiva.catalog.ProductStatus;
import io.github.nonoleekr.cartiva.identity.Role;
import io.github.nonoleekr.cartiva.identity.User;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderService = new OrderService(orderRepository, cartRepository, productRepository);
    }

    @Test
    void checksOutWithSnapshotsDeductsStockAndClearsCart() {
        UUID customerId = UUID.randomUUID();
        User customer = user("customer", "Customer");
        Product product = product("Mechanical Keyboard", new BigDecimal("249.90"), 5);
        Cart cart = new Cart(customer);
        cart.add(new CartItem(cart, product, 2));
        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
        when(productRepository.findByIdForUpdate(product.getId())).thenReturn(Optional.of(product));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));
        CheckoutRequest request = new CheckoutRequest(
            "Customer",
            "1 Market Street",
            "Kuala Lumpur",
            "50000",
            "Malaysia"
        );

        OrderResponse response = orderService.checkout(customerId, request);

        assertThat(response.status()).isEqualTo(OrderStatus.CONFIRMED);
        assertThat(response.totalAmount()).isEqualByComparingTo("499.80");
        assertThat(response.items()).singleElement()
            .satisfies(item -> {
                assertThat(item.productName()).isEqualTo("Mechanical Keyboard");
                assertThat(item.unitPrice()).isEqualByComparingTo("249.90");
                assertThat(item.quantity()).isEqualTo(2);
            });
        assertThat(product.getStockQuantity()).isEqualTo(3);
        assertThat(cart.getItems()).isEmpty();
        verify(cartRepository).save(cart);
    }

    @Test
    void rejectsCheckoutWhenStockChangedAfterAddingToCart() {
        UUID customerId = UUID.randomUUID();
        User customer = user("customer", "Customer");
        Product product = product("Limited Item", new BigDecimal("10.00"), 1);
        Cart cart = new Cart(customer);
        cart.add(new CartItem(cart, product, 2));
        when(cartRepository.findByCustomerId(customerId)).thenReturn(Optional.of(cart));
        when(productRepository.findByIdForUpdate(product.getId())).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> orderService.checkout(
            customerId,
            new CheckoutRequest("Customer", "1 Street", "City", "10000", "Malaysia")
        ))
            .isInstanceOf(CheckoutException.class)
            .hasMessage("Insufficient stock for Limited Item");

        verify(orderRepository, never()).save(any(Order.class));
        assertThat(cart.getItems()).hasSize(1);
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
