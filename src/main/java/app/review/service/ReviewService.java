package app.review.service;

import app.model.User;
import app.review.client.dto.CreateReviewRequestDto;
import app.review.client.dto.ReviewDto;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for interacting with the external review microservice.
 */
public interface ReviewService {

    /**
     * Builds a review request DTO from internal data.
     */
    CreateReviewRequestDto getCreateReviewRequestDto(UUID id, String content, User user);

    /**
     * Retrieves all reviews for a listing from the review service.
     */
    List<ReviewDto> getReviewDtos(UUID id);

    /**
     * Sends a new review creation request to the review service.
     */
    void createReview(CreateReviewRequestDto requestDto);

    /**
     * Requests the deletion of a review from the review service.
     */
    void deleteReview(UUID reviewId, User user);

    /**
     * Resolves and assigns usernames to each ReviewDto based on userId.
     */
    void addUsernameToDto(List<ReviewDto> reviews);
}
