package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class CartTest {
    @Test
    void addsNewItemsAtTheHead() {
        Cart cart = new Cart();
        Product first = new Product("P1", "Keyboard", 25.00, 10);
        Product second = new Product("P2", "Mouse", 12.50, 20);

        cart.addItem(first, 1);
        cart.addItem(second, 2);

        assertEquals("P2", cart.getHead().getProduct().getId());
        assertEquals("P1", cart.getHead().getNext().getProduct().getId());
    }

    @Test
    void combinesQuantitiesForTheSameProductId() {
        Cart cart = new Cart();
        Product product = new Product("P1", "Keyboard", 25.00, 10);

        cart.addItem(product, 2);
        cart.addItem(product, 3);

        assertEquals(5, cart.getHead().getQuantity());
        assertNull(cart.getHead().getNext());
    }

    @Test
    void removesHeadAndNonHeadItems() {
        Cart cart = new Cart();
        cart.addItem(new Product("P1", "Keyboard", 25.00, 10), 1);
        cart.addItem(new Product("P2", "Mouse", 12.50, 20), 1);
        cart.addItem(new Product("P3", "Monitor", 199.99, 5), 1);

        cart.removeItem("P2");
        assertEquals("P3", cart.getHead().getProduct().getId());
        assertEquals("P1", cart.getHead().getNext().getProduct().getId());

        cart.removeItem("P3");
        assertEquals("P1", cart.getHead().getProduct().getId());
    }

    @Test
    void calculatesTotalFromPriceAndQuantity() {
        Cart cart = new Cart();
        cart.addItem(new Product("P1", "Keyboard", 25.00, 10), 2);
        cart.addItem(new Product("P2", "Mouse", 12.50, 20), 3);

        assertEquals(87.50, cart.calculateTotal(), 0.001);
    }
}
