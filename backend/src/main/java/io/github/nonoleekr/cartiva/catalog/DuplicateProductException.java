package io.github.nonoleekr.cartiva.catalog;

public class DuplicateProductException extends RuntimeException {
    public DuplicateProductException(String message) {
        super(message);
    }
}
