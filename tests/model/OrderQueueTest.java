package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class OrderQueueTest {
    @Test
    void processesOrdersInFirstInFirstOutOrder() {
        OrderQueue queue = new OrderQueue();
        Order first = new Order("first-user", new Cart(), 10.00);
        Order second = new Order("second-user", new Cart(), 20.00);

        queue.placeOrder(first);
        queue.placeOrder(second);

        assertEquals(first, queue.processNextOrder());
        assertEquals(second, queue.processNextOrder());
        assertTrue(queue.isEmpty());
        assertNull(queue.processNextOrder());
    }
}
