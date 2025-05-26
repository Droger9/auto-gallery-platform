package app.service;

import app.model.Review;

import java.util.List;
import java.util.UUID;

/**
 * Service interface for managing reviews on listings
 */
public interface ReviewService {

    /**
     * Retrieves all reviews associated with a specific listing
     *
     * @param listingId the ID of the listing
     * @return list of reviews
     */
    List<Review> getReviewsByListing(UUID listingId);

    /**
     * Creates a new review for a listing by a specific user
     *
     * @param listingId the ID of the listing
     * @param userId the ID of the user posting the review
     * @param content the content of the review
     * @return the saved review
     */
    Review createReview(UUID listingId, UUID userId, String content);

    /**
     * Deletes a review if the requester is the owner or an admin
     *
     * @param reviewId the ID of the review
     * @param userId the ID of the current user
     * @param isAdmin whether the user is an admin
     */
    void deleteReview(UUID reviewId, UUID userId, boolean isAdmin);
}
