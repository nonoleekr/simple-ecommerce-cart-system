package io.github.nonoleekr.cartiva.order;

import io.github.nonoleekr.cartiva.cart.Cart;
import io.github.nonoleekr.cartiva.cart.CartItem;
import io.github.nonoleekr.cartiva.cart.CartRepository;
import io.github.nonoleekr.cartiva.catalog.Product;
import io.github.nonoleekr.cartiva.catalog.ProductRepository;
import io.github.nonoleekr.cartiva.catalog.ProductStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;

    public OrderService(
        OrderRepository orderRepository,
        CartRepository cartRepository,
        ProductRepository productRepository
    ) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
    }

    @Transactional
    public OrderResponse checkout(UUID customerId, CheckoutRequest request) {
        Cart cart = cartRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new CheckoutException("Cart was not found"));
        if (cart.getItems().isEmpty()) {
            throw new CheckoutException("Cart is empty");
        }

        Order order = new Order(cart.getCustomer(), request);
        for (CartItem cartItem : cart.getItems()) {
            Product product = productRepository.findByIdForUpdate(cartItem.getProduct().getId())
                .orElseThrow(() -> new CheckoutException("A cart product no longer exists"));
            if (product.getStatus() != ProductStatus.ACTIVE) {
                throw new CheckoutException(product.getName() + " is no longer available");
            }
            if (cartItem.getQuantity() > product.getStockQuantity()) {
                throw new CheckoutException("Insufficient stock for " + product.getName());
            }

            product.decreaseStock(cartItem.getQuantity());
            order.addItem(new OrderItem(order, product, cartItem.getQuantity()));
        }

        Order savedOrder = orderRepository.save(order);
        cart.clear();
        cartRepository.save(cart);
        return OrderResponse.from(savedOrder);
    }

    @Transactional(readOnly = true)
    public List<OrderResponse> getOrderHistory(UUID customerId) {
        return orderRepository.findAllByCustomerIdOrderByCreatedAtDesc(customerId).stream()
            .map(OrderResponse::from)
            .toList();
    }
}
