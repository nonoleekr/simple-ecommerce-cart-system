package model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.Test;

class SerializationTest {
    @Test
    void productRoundTripsThroughLegacyCsvFormat() {
        Product original = new Product("P1", "Keyboard", 25.50, 8);

        Product restored = Product.fromString(original.toString());

        assertEquals(original.getId(), restored.getId());
        assertEquals(original.getName(), restored.getName());
        assertEquals(original.getPrice(), restored.getPrice(), 0.001);
        assertEquals(original.getStock(), restored.getStock());
    }

    @Test
    void userRoundTripsThroughLegacyCsvFormat() {
        User original = new User("student", "secret123");

        User restored = User.fromString(original.toString());

        assertEquals(original.getUsername(), restored.getUsername());
        assertEquals(original.getPassword(), restored.getPassword());
    }

    @Test
    void malformedLegacyRecordsAreRejected() {
        assertNull(Product.fromString("not,a,product"));
        assertNull(User.fromString("not-a-user"));
    }
}
