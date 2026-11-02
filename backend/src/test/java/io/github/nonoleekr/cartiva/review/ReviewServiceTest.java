package io.github.nonoleekr.cartiva.review;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.nonoleekr.cartiva.catalog.Category;
import io.github.nonoleekr.cartiva.catalog.Product;
import io.github.nonoleekr.cartiva.catalog.ProductRepository;
import io.github.nonoleekr.cartiva.catalog.ProductStatus;
import io.github.nonoleekr.cartiva.identity.Role;
import io.github.nonoleekr.cartiva.identity.User;
import io.github.nonoleekr.cartiva.identity.UserRepository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {
    @Mock ProductReviewRepository reviewRepository;
    @Mock ProductRepository productRepository;
    @Mock UserRepository userRepository;
    private ReviewService reviewService;

    @BeforeEach void setUp() { reviewService = new ReviewService(reviewRepository, productRepository, userRepository); }

    @Test
    void calculatesRoundedReviewSummary() {
        Product product = product();
        User first = user("first"); User second = user("second");
        when(productRepository.findByIdAndStatus(product.getId(), ProductStatus.ACTIVE)).thenReturn(Optional.of(product));
        when(reviewRepository.findAllByProductIdOrderByCreatedAtDesc(product.getId())).thenReturn(List.of(
            new ProductReview(product, first, 5, "Excellent", "Loved it"),
            new ProductReview(product, second, 4, "Good", "Works well")
        ));

        ProductReviewsResponse response = reviewService.getReviews(product.getId());

        assertThat(response.averageRating()).isEqualTo(4.5);
        assertThat(response.reviewCount()).isEqualTo(2);
    }

    @Test
    void createsTrimmedReview() {
        Product product = product(); UUID customerId = UUID.randomUUID(); User customer = user("customer");
        when(reviewRepository.existsByProductIdAndCustomerId(product.getId(), customerId)).thenReturn(false);
        when(productRepository.findByIdAndStatus(product.getId(), ProductStatus.ACTIVE)).thenReturn(Optional.of(product));
        when(userRepository.findById(customerId)).thenReturn(Optional.of(customer));
        when(reviewRepository.save(any(ProductReview.class))).thenAnswer(invocation -> invocation.getArgument(0));

        ReviewResponse response = reviewService.create(product.getId(), customerId, new CreateReviewRequest(5, " Great ", " Very useful "));

        assertThat(response.title()).isEqualTo("Great");
        assertThat(response.comment()).isEqualTo("Very useful");
    }

    @Test
    void rejectsSecondReviewFromSameCustomer() {
        UUID productId = UUID.randomUUID(); UUID customerId = UUID.randomUUID();
        when(reviewRepository.existsByProductIdAndCustomerId(productId, customerId)).thenReturn(true);

        assertThatThrownBy(() -> reviewService.create(productId, customerId, new CreateReviewRequest(4, "Good", "Good product")))
            .isInstanceOf(ReviewOperationException.class)
            .hasMessage("You have already reviewed this product");
    }

    private User user(String username) { return new User(username, username + "@example.com", "hash", username, mock(Role.class)); }
    private Product product() { return new Product(user("seller"), mock(Category.class), "Keyboard", "keyboard", "Description", BigDecimal.TEN, 5, ProductStatus.ACTIVE); }
}
