package app.review.service;

import app.model.User;
import app.review.client.dto.CreateReviewRequestDto;
import app.review.client.dto.ReviewDto;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for interacting with the external review microservice
 */
public interface ReviewService {

    /**
     * Builds a review request DTO from listing, content, and user data
     *
     * @param id the ID of the listing
     * @param content the content of the review
     * @param user the user posting the review
     * @return the populated CreateReviewRequestDto
     */
    CreateReviewRequestDto getCreateReviewRequestDto(UUID id, String content, User user);

    /**
     * Retrieves reviews for a given listing
     *
     * @param id the ID of the listing
     * @return list of review DTOs
     */
    List<ReviewDto> getReviewDtos(UUID id);

    /**
     * Sends a request to create a new review
     *
     * @param requestDto the review request DTO
     */
    void createReview(CreateReviewRequestDto requestDto);

    /**
     * Deletes a review via the review microservice
     *
     * @param reviewId the review ID
     * @param user the user performing the deletion
     */
    void deleteReview(UUID reviewId, User user);

    /**
     * Adds the username to each review DTO by resolving userId from the main service
     *
     * @param reviews the list of reviews to enrich
     */
    void addUsernameToDto(List<ReviewDto> reviews);
}

