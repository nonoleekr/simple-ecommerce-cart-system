package io.github.nonoleekr.cartiva.cart;

import io.github.nonoleekr.cartiva.catalog.Product;
import io.github.nonoleekr.cartiva.catalog.ProductRepository;
import io.github.nonoleekr.cartiva.catalog.ProductStatus;
import io.github.nonoleekr.cartiva.identity.User;
import io.github.nonoleekr.cartiva.identity.UserRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CartService {
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
        CartRepository cartRepository,
        ProductRepository productRepository,
        UserRepository userRepository
    ) {
        this.cartRepository = cartRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public CartResponse getCart(UUID customerId) {
        return CartResponse.from(getOrCreateCart(customerId));
    }

    @Transactional
    public CartResponse addItem(UUID customerId, AddCartItemRequest request) {
        Cart cart = getOrCreateCart(customerId);
        Product product = findActiveProduct(request.productId());
        CartItem existingItem = findItem(cart, product.getId());
        int desiredQuantity = request.quantity();
        if (existingItem != null) {
            desiredQuantity += existingItem.getQuantity();
        }
        validateStock(product, desiredQuantity);

        if (existingItem == null) {
            cart.add(new CartItem(cart, product, desiredQuantity));
        } else {
            existingItem.setQuantity(desiredQuantity);
        }
        return CartResponse.from(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse updateItem(UUID customerId, UUID productId, int quantity) {
        Cart cart = findCart(customerId);
        CartItem item = findRequiredItem(cart, productId);
        validateStock(item.getProduct(), quantity);
        item.setQuantity(quantity);
        return CartResponse.from(cartRepository.save(cart));
    }

    @Transactional
    public CartResponse removeItem(UUID customerId, UUID productId) {
        Cart cart = findCart(customerId);
        cart.remove(findRequiredItem(cart, productId));
        return CartResponse.from(cartRepository.save(cart));
    }

    private Cart getOrCreateCart(UUID customerId) {
        return cartRepository.findByCustomerId(customerId).orElseGet(() -> {
            User customer = userRepository.findById(customerId)
                .orElseThrow(() -> new CartOperationException("Customer account was not found"));
            return cartRepository.save(new Cart(customer));
        });
    }

    private Cart findCart(UUID customerId) {
        return cartRepository.findByCustomerId(customerId)
            .orElseThrow(() -> new CartOperationException("Cart was not found"));
    }

    private Product findActiveProduct(UUID productId) {
        return productRepository.findByIdAndStatus(productId, ProductStatus.ACTIVE)
            .orElseThrow(() -> new CartOperationException("Product is not available"));
    }

    private CartItem findRequiredItem(Cart cart, UUID productId) {
        CartItem item = findItem(cart, productId);
        if (item == null) {
            throw new CartOperationException("Product is not in the cart");
        }
        return item;
    }

    private CartItem findItem(Cart cart, UUID productId) {
        return cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst()
            .orElse(null);
    }

    private void validateStock(Product product, int quantity) {
        if (product.getStatus() != ProductStatus.ACTIVE) {
            throw new CartOperationException("Product is not available");
        }
        if (quantity > product.getStockQuantity()) {
            throw new CartOperationException("Requested quantity exceeds available stock");
        }
    }
}
