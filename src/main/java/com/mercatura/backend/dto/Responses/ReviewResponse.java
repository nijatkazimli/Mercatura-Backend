package com.mercatura.backend.dto.Responses;

import com.mercatura.backend.entity.Image;
import com.mercatura.backend.entity.Review;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private UUID id;
    private String content;
    private Integer rating;
    private String authorFullName;
    private String authorProfileImage;
    private UUID productId;

    public ReviewResponse(Review review) {
        this.id = review.getId();
        this.content = review.getContent();
        this.rating = review.getRating();
        this.authorFullName = review.getAuthor().getFirstName() + " " + review.getAuthor().getLastName();
        Image profileImage = review.getAuthor().getProfileImage();
        if (profileImage != null) {
            this.authorProfileImage = profileImage.getImageUrl();
        }
        this.productId = review.getProduct().getId();
    }
}
