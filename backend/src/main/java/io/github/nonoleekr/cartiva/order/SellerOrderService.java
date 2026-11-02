package io.github.nonoleekr.cartiva.order;

import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SellerOrderService {
    private final SellerFulfillmentRepository fulfillmentRepository;
    public SellerOrderService(SellerFulfillmentRepository fulfillmentRepository) { this.fulfillmentRepository = fulfillmentRepository; }

    @Transactional(readOnly = true)
    public List<SellerOrderResponse> getOrders(UUID sellerId) {
        return fulfillmentRepository.findAllBySellerIdOrderByUpdatedAtDesc(sellerId).stream().map(SellerOrderResponse::from).toList();
    }

    @Transactional
    public SellerOrderResponse updateStatus(UUID sellerId, UUID orderId, OrderStatus nextStatus) {
        SellerFulfillment fulfillment = fulfillmentRepository.findByOrderIdAndSellerId(orderId, sellerId)
            .orElseThrow(() -> new SellerOrderException("Seller order was not found"));
        if (!isAllowedTransition(fulfillment.getStatus(), nextStatus)) {
            throw new SellerOrderException("Invalid fulfillment status transition");
        }
        fulfillment.setStatus(nextStatus);
        return SellerOrderResponse.from(fulfillmentRepository.save(fulfillment));
    }

    private boolean isAllowedTransition(OrderStatus current, OrderStatus next) {
        if (current == next) return true;
        return switch (current) {
            case CONFIRMED -> next == OrderStatus.PROCESSING || next == OrderStatus.CANCELLED;
            case PROCESSING -> next == OrderStatus.SHIPPED || next == OrderStatus.CANCELLED;
            case SHIPPED -> next == OrderStatus.DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}
