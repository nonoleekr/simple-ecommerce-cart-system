package io.github.nonoleekr.cartiva.review;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreateReviewRequest(
    @Min(1) @Max(5) int rating,
    @NotBlank @Size(max = 120) String title,
    @NotBlank @Size(max = 2000) String comment
) {}
