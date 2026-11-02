package io.github.nonoleekr.cartiva.catalog;

import io.github.nonoleekr.cartiva.identity.User;
import io.github.nonoleekr.cartiva.identity.UserRepository;
import io.github.nonoleekr.cartiva.identity.UserStatus;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CatalogService {
    private static final Map<String, Sort> ALLOWED_SORTS = Map.of(
        "newest", Sort.by(Sort.Direction.DESC, "createdAt"),
        "price-asc", Sort.by(Sort.Direction.ASC, "price"),
        "price-desc", Sort.by(Sort.Direction.DESC, "price"),
        "name", Sort.by(Sort.Direction.ASC, "name")
    );

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public CatalogService(
        ProductRepository productRepository,
        CategoryRepository categoryRepository,
        UserRepository userRepository
    ) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<ProductResponse> browse(
        String query,
        String categorySlug,
        String sort,
        int page,
        int size
    ) {
        Sort selectedSort = ALLOWED_SORTS.getOrDefault(sort, ALLOWED_SORTS.get("newest"));
        PageRequest pageRequest = PageRequest.of(page, Math.min(size, 100), selectedSort);
        Page<Product> products = productRepository.searchCatalog(
            ProductStatus.ACTIVE,
            normalizeOptional(query),
            normalizeOptional(categorySlug),
            pageRequest
        );
        return PageResponse.from(products, ProductResponse::from);
    }

    @Transactional(readOnly = true)
    public ProductResponse findActiveProduct(String slug) {
        return productRepository.findBySlugAndStatus(slug, ProductStatus.ACTIVE)
            .map(ProductResponse::from)
            .orElseThrow(() -> new CatalogNotFoundException("Product was not found"));
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> listCategories() {
        return categoryRepository.findAll(Sort.by("name")).stream()
            .map(CategoryResponse::from)
            .toList();
    }

    @Transactional
    public ProductResponse createDraft(UUID sellerId, CreateProductRequest request) {
        User seller = userRepository.findById(sellerId)
            .filter(user -> user.getStatus() == UserStatus.ACTIVE)
            .orElseThrow(() -> new CatalogNotFoundException("Seller account was not found"));
        String categorySlug = request.categorySlug().trim().toLowerCase(Locale.ROOT);
        Category category = categoryRepository.findBySlug(categorySlug)
            .orElseThrow(() -> new CatalogNotFoundException("Category was not found"));
        String name = request.name().trim();
        String slug = Slugifier.slugify(name);
        if (slug.isBlank()) {
            throw new IllegalArgumentException("Product name must contain letters or numbers");
        }
        if (productRepository.existsBySlug(slug)) {
            throw new DuplicateProductException("A product with this name already exists");
        }

        Product product = new Product(
            seller,
            category,
            name,
            slug,
            request.description().trim(),
            request.price(),
            request.stockQuantity(),
            ProductStatus.DRAFT
        );
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> listSellerProducts(UUID sellerId) {
        return productRepository.findAllBySellerIdOrderByCreatedAtDesc(sellerId).stream()
            .map(ProductResponse::from)
            .toList();
    }

    @Transactional
    public ProductResponse updateSellerProduct(UUID sellerId, UUID productId, UpdateProductRequest request) {
        Product product = findSellerProduct(sellerId, productId);
        Category category = findCategory(request.categorySlug());
        String name = request.name().trim();
        String slug = Slugifier.slugify(name);
        if (slug.isBlank()) throw new IllegalArgumentException("Product name must contain letters or numbers");
        if (productRepository.existsBySlugAndIdNot(slug, productId)) {
            throw new DuplicateProductException("A product with this name already exists");
        }
        product.update(category, name, slug, request.description().trim(), request.price(), request.stockQuantity());
        return ProductResponse.from(productRepository.save(product));
    }

    @Transactional
    public ProductResponse updateSellerProductStatus(UUID sellerId, UUID productId, ProductStatus status) {
        Product product = findSellerProduct(sellerId, productId);
        if (status == ProductStatus.ACTIVE && product.getStockQuantity() < 1) {
            throw new IllegalArgumentException("Add inventory before publishing this product");
        }
        product.setStatus(status);
        return ProductResponse.from(productRepository.save(product));
    }

    private Product findSellerProduct(UUID sellerId, UUID productId) {
        return productRepository.findByIdAndSellerId(productId, sellerId)
            .orElseThrow(() -> new CatalogNotFoundException("Seller product was not found"));
    }

    private Category findCategory(String categorySlug) {
        return categoryRepository.findBySlug(categorySlug.trim().toLowerCase(Locale.ROOT))
            .orElseThrow(() -> new CatalogNotFoundException("Category was not found"));
    }

    private String normalizeOptional(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toLowerCase(Locale.ROOT);
    }
}
