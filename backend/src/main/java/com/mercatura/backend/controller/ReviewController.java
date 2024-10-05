package com.mercatura.backend.controller;

import com.mercatura.backend.dto.ReviewResponse;
import com.mercatura.backend.dto.UUIDResponse;
import com.mercatura.backend.entity.Review;
import com.mercatura.backend.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/review")
@Validated
public class ReviewController {
    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Tag(name = "Review")
    @Operation(summary = "Gets all available reviews.")
    @GetMapping
    public List<ReviewResponse> getAllReviews(
            @Parameter(description = "Page number for pagination. Default 0.")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size for pagination. Default 200.")
            @RequestParam(defaultValue = "200") Integer size
    ) {
        return reviewService.getAllReviews(page, size);
    }

    @Tag(name = "Review")
    @Operation(summary = "Gets the reviews of the product with the given ID.")
    @GetMapping("/get")
    public List<ReviewResponse> getReviewsByProductId(
            @Parameter(description = "ID of the products reviews of which to be retrieved.")
            @RequestParam UUID productId,
            @Parameter(description = "Page number for pagination. Default 0.")
            @RequestParam(defaultValue = "0") Integer page,
            @Parameter(description = "Page size for pagination. Default 15.")
            @RequestParam(defaultValue = "15") Integer size)
    {
        return reviewService.getReviewsByProductId(productId, page, size);
    }

    @Tag(name = "Review")
    @Operation(summary = "Adds a new review")
    @PostMapping()
    public UUIDResponse addReview(
            @Valid @RequestBody Review review,
            @Parameter(
              description = "ID of the author."
            ) @RequestParam UUID authorId,
            @Parameter(
              description = "ID of the product on which the review is added."
            ) @RequestParam UUID productId
    ) throws BadRequestException {
        if (1 <= review.getRating() && review.getRating() <= 5) {
            return reviewService.addReview(review, authorId, productId);
        } else {
            throw new BadRequestException("Invalid rating. Rating should be between 1 and 5");
        }
    }

    @Tag(name = "Review")
    @Operation(summary = "Deletes the review with the given ID.")
    @DeleteMapping("/{id}")
    public void deleteReviewById(@Parameter(
                                    description = "ID of the review to be deleted."
                                ) @PathVariable UUID id) {
        reviewService.deleteReviewById(id);
    }
}
