package io.github.nonoleekr.cartiva.catalog;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import io.github.nonoleekr.cartiva.identity.Role;
import io.github.nonoleekr.cartiva.identity.User;
import io.github.nonoleekr.cartiva.identity.UserRepository;
import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private UserRepository userRepository;

    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
        catalogService = new CatalogService(productRepository, categoryRepository, userRepository);
    }

    @Test
    void createsANormalizedDraftForAnActiveSeller() {
        UUID sellerId = UUID.randomUUID();
        User seller = new User("seller", "seller@example.com", "hash", "Great Seller", mock(Role.class));
        Category category = mock(Category.class);
        when(category.getSlug()).thenReturn("electronics");
        when(userRepository.findById(sellerId)).thenReturn(Optional.of(seller));
        when(categoryRepository.findBySlug("electronics")).thenReturn(Optional.of(category));
        when(productRepository.save(any(Product.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ProductResponse response = catalogService.createDraft(
            sellerId,
            new CreateProductRequest(
                "  Café Headphones  ",
                " ELECTRONICS ",
                "  Wireless headphones  ",
                new BigDecimal("129.90"),
                8
            )
        );

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productRepository).save(productCaptor.capture());
        Product product = productCaptor.getValue();
        assertThat(product.getSlug()).isEqualTo("cafe-headphones");
        assertThat(product.getStatus()).isEqualTo(ProductStatus.DRAFT);
        assertThat(product.getDescription()).isEqualTo("Wireless headphones");
        assertThat(response.sellerName()).isEqualTo("Great Seller");
    }

    @Test
    void normalizesSearchAndCapsPageSize() {
        when(productRepository.searchCatalog(any(), any(), any(), any()))
            .thenReturn(Page.empty(PageRequest.of(0, 100)));

        catalogService.browse("  LAPTOP  ", " Electronics ", "unsupported", 0, 500);

        ArgumentCaptor<PageRequest> pageCaptor = ArgumentCaptor.forClass(PageRequest.class);
        verify(productRepository).searchCatalog(
            eq(ProductStatus.ACTIVE),
            eq("laptop"),
            eq("electronics"),
            pageCaptor.capture()
        );
        assertThat(pageCaptor.getValue().getPageSize()).isEqualTo(100);
        assertThat(pageCaptor.getValue().getSort().getOrderFor("createdAt").isDescending()).isTrue();
    }

    @Test
    void updatesOnlySellerOwnedProduct() {
        UUID sellerId = UUID.randomUUID();
        Product product = product(ProductStatus.DRAFT, 2);
        Category category = mock(Category.class);
        when(productRepository.findByIdAndSellerId(product.getId(), sellerId)).thenReturn(Optional.of(product));
        when(categoryRepository.findBySlug("fashion")).thenReturn(Optional.of(category));
        when(productRepository.save(product)).thenReturn(product);

        ProductResponse response = catalogService.updateSellerProduct(sellerId, product.getId(), new UpdateProductRequest(
            "Updated Bag", " FASHION ", " Updated description ", new BigDecimal("89.90"), 7
        ));

        assertThat(response.name()).isEqualTo("Updated Bag");
        assertThat(response.slug()).isEqualTo("updated-bag");
        assertThat(response.stockQuantity()).isEqualTo(7);
    }

    @Test
    void requiresInventoryBeforePublishing() {
        UUID sellerId = UUID.randomUUID();
        Product product = product(ProductStatus.DRAFT, 0);
        when(productRepository.findByIdAndSellerId(product.getId(), sellerId)).thenReturn(Optional.of(product));

        org.assertj.core.api.Assertions.assertThatThrownBy(() ->
            catalogService.updateSellerProductStatus(sellerId, product.getId(), ProductStatus.ACTIVE)
        ).isInstanceOf(IllegalArgumentException.class).hasMessage("Add inventory before publishing this product");
    }

    private Product product(ProductStatus status, int stock) {
        User seller = new User("seller-two", "seller-two@example.com", "hash", "Seller", mock(Role.class));
        Category category = mock(Category.class);
        return new Product(seller, category, "Bag", "bag", "Description", BigDecimal.TEN, stock, status);
    }
}
