package com.mercatura.backend.service;

import com.mercatura.backend.dto.Responses.ReviewResponse;
import com.mercatura.backend.dto.Responses.UUIDResponse;
import com.mercatura.backend.entity.ApplicationUser;
import com.mercatura.backend.entity.Product;
import com.mercatura.backend.entity.Review;
import com.mercatura.backend.repository.ProductRepository;
import com.mercatura.backend.repository.ReviewRepository;
import com.mercatura.backend.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewServiceTest {

    @Mock
    private ReviewRepository reviewRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ReviewService reviewService;

    private AutoCloseable closeable;

    @BeforeEach
    void setUp() {
        closeable = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void tearDown() throws Exception {
        closeable.close();
    }

    @Test
    void getAllReviews_ShouldReturnReviewResponses() {
        ApplicationUser author = new ApplicationUser();
        Product product = new Product();
        Review review = new Review();
        review.setAuthor(author);
        review.setProduct(product);
        Page<Review> reviewPage = new PageImpl<>(List.of(review));
        when(reviewRepository.findAll(any(PageRequest.class))).thenReturn(reviewPage);

        List<ReviewResponse> responses = reviewService.getAllReviews(0, 10);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(reviewRepository).findAll(any(PageRequest.class));
    }

    @Test
    void getReviewsByProductId_ShouldReturnReviewResponses() {
        ApplicationUser author = new ApplicationUser();
        author.setFirstName("Mercatura");
        author.setLastName("Alexandre");
        Product product = new Product();
        Review review = new Review();
        review.setAuthor(author);
        review.setProduct(product);
        UUID productId = UUID.randomUUID();
        Page<Review> reviewPage = new PageImpl<>(List.of(review));
        when(reviewRepository.findByProductId(eq(productId), any(PageRequest.class))).thenReturn(reviewPage);

        List<ReviewResponse> responses = reviewService.getReviewsByProductId(productId, 0, 10);

        assertNotNull(responses);
        assertEquals(1, responses.size());
        verify(reviewRepository).findByProductId(eq(productId), any(PageRequest.class));
    }

    @Test
    void addReview_ShouldReturnUUIDResponse() {
        UUID userId = UUID.randomUUID();
        UUID productId = UUID.randomUUID();
        Review review = new Review();
        ApplicationUser user = new ApplicationUser();
        Product product = new Product();
        product.setId(productId);
        product.setReviews(new ArrayList<>());
        review.setAuthor(user);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(productRepository.findById(productId)).thenReturn(Optional.of(product));
        when(reviewRepository.save(any(Review.class))).thenReturn(review);

        UUIDResponse response = reviewService.addReview(review, userId, productId);

        assertNotNull(response);
        verify(reviewRepository).save(review);
        verify(productRepository).save(product);
    }

    @Test
    void deleteReviewById_ShouldUpdateProductRating() {
        UUID reviewId = UUID.randomUUID();
        Review review = new Review();
        Product product = new Product();
        product.setReviews(List.of(review));
        review.setProduct(product);
        review.setRating(5);

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.of(review));

        reviewService.deleteReviewById(reviewId);

        verify(reviewRepository).deleteById(reviewId);
        verify(productRepository).save(product);
    }

    @Test
    void deleteReviewById_ShouldThrowException_WhenReviewNotFound() {
        UUID reviewId = UUID.randomUUID();

        when(reviewRepository.findById(reviewId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                reviewService.deleteReviewById(reviewId));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}
