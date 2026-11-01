package io.github.nonoleekr.cartiva.catalog;

public class CatalogNotFoundException extends RuntimeException {
    public CatalogNotFoundException(String message) {
        super(message);
    }
}
