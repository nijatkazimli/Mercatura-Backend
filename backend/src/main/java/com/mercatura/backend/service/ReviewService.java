package com.mercatura.backend.service;

import com.mercatura.backend.dto.ReviewResponse;
import com.mercatura.backend.dto.UUIDResponse;
import com.mercatura.backend.entity.ApplicationUser;
import com.mercatura.backend.entity.Product;
import com.mercatura.backend.entity.Review;
import com.mercatura.backend.repository.ProductRepository;
import com.mercatura.backend.repository.ReviewRepository;
import com.mercatura.backend.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;

    public ReviewService(
            ReviewRepository reviewRepository,
            UserRepository userRepository,
            ProductRepository productRepository
    ) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }

    public List<ReviewResponse> getAllReviews(Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findAll(pageable);
        return reviewPage.stream().map(ReviewResponse::new).collect(Collectors.toList());
    }

    public List<ReviewResponse> getReviewsByProductId(UUID productId, Integer page, Integer size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviewPage = reviewRepository.findByProductId(productId, pageable);
        return  reviewPage.stream().map(ReviewResponse::new).collect(Collectors.toList());
    }

    public UUIDResponse addReview(Review review, UUID userId, UUID productId) {
        ApplicationUser author = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                    String.format("User with id %s not found", userId)));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Product with id %s not found", productId)));

        review.setProduct(product);
        review.setAuthor(author);
        Review savedReview = reviewRepository.save(review);

        Double newRating = product.getReviews().stream().mapToDouble(Review::getRating).average().orElse(0.0);
        product.setRating(newRating);
        productRepository.save(product);

        return new UUIDResponse(savedReview.getId());
    }

    public void deleteReviewById(UUID id) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        String.format("Review with id %s not found", id)));
        Product product = review.getProduct();
        reviewRepository.deleteById(id);
        Double newRating = product.getReviews().stream()
                .mapToDouble(Review::getRating)
                .average()
                .orElse(0.0);
        product.setRating(newRating);
        productRepository.save(product);
    }
}
