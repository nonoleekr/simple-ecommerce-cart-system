package io.github.nonoleekr.cartiva.catalog;

import java.util.UUID;

public record CategoryResponse(UUID id, String name, String slug) {
    static CategoryResponse from(Category category) {
        return new CategoryResponse(category.getId(), category.getName(), category.getSlug());
    }
}
