package io.github.nonoleekr.cartiva.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.nonoleekr.cartiva.catalog.Category;
import io.github.nonoleekr.cartiva.catalog.Product;
import io.github.nonoleekr.cartiva.catalog.ProductStatus;
import io.github.nonoleekr.cartiva.identity.Role;
import io.github.nonoleekr.cartiva.identity.User;
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
class SellerOrderServiceTest {
    @Mock SellerFulfillmentRepository fulfillmentRepository;
    private SellerOrderService service;

    @BeforeEach void setUp() { service = new SellerOrderService(fulfillmentRepository); }

    @Test
    void returnsOnlyItemsOwnedBySeller() {
        User seller = user("seller"); User otherSeller = user("other");
        Order order = order();
        order.addItem(new OrderItem(order, product(seller, "Keyboard", "25.00"), 2));
        order.addItem(new OrderItem(order, product(otherSeller, "Lamp", "10.00"), 1));
        SellerFulfillment fulfillment = new SellerFulfillment(order, seller);
        when(fulfillmentRepository.findAllBySellerIdOrderByUpdatedAtDesc(seller.getId())).thenReturn(List.of(fulfillment));

        SellerOrderResponse response = service.getOrders(seller.getId()).getFirst();

        assertThat(response.items()).singleElement().extracting(OrderItemResponse::productName).isEqualTo("Keyboard");
        assertThat(response.sellerTotal()).isEqualByComparingTo("50.00");
    }

    @Test
    void advancesFulfillmentThroughAllowedSequence() {
        User seller = user("seller"); Order order = order(); SellerFulfillment fulfillment = new SellerFulfillment(order, seller);
        when(fulfillmentRepository.findByOrderIdAndSellerId(order.getId(), seller.getId())).thenReturn(Optional.of(fulfillment));
        when(fulfillmentRepository.save(fulfillment)).thenReturn(fulfillment);

        SellerOrderResponse response = service.updateStatus(seller.getId(), order.getId(), OrderStatus.PROCESSING);

        assertThat(response.status()).isEqualTo(OrderStatus.PROCESSING);
    }

    @Test
    void rejectsSkippingFulfillmentSteps() {
        User seller = user("seller"); Order order = order(); SellerFulfillment fulfillment = new SellerFulfillment(order, seller);
        when(fulfillmentRepository.findByOrderIdAndSellerId(order.getId(), seller.getId())).thenReturn(Optional.of(fulfillment));

        assertThatThrownBy(() -> service.updateStatus(seller.getId(), order.getId(), OrderStatus.DELIVERED))
            .isInstanceOf(SellerOrderException.class)
            .hasMessage("Invalid fulfillment status transition");
    }

    private Order order() { return new Order(user("customer"), new CheckoutRequest("Customer", "1 Main Street", "Kuala Lumpur", "50000", "Malaysia")); }
    private User user(String username) { return new User(username, username + "@example.com", "hash", username, mock(Role.class)); }
    private Product product(User seller, String name, String price) { return new Product(seller, mock(Category.class), name, name.toLowerCase(), "Description", new BigDecimal(price), 10, ProductStatus.ACTIVE); }
}
