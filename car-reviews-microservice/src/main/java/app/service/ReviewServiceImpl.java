package app.service;

import app.model.Review;
import app.repository.ReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Autowired
    public ReviewServiceImpl(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    @Override
    public List<Review> getReviewsByListing(UUID listingId) {
        return reviewRepository.findAllByListingId(listingId);
    }

    @Override
    public Review createReview(UUID listingId, UUID userId, String content) {
        Review review = new Review();
        review.setListingId(listingId);
        review.setUserId(userId);
        review.setContent(content);
        review.setCreatedAt(LocalDateTime.now());
        return reviewRepository.save(review);
    }

    @Override
    public void deleteReview(UUID reviewId, UUID userId, boolean isAdmin) {
        Review review = reviewRepository.findById(reviewId);
        if (!isAdmin && !review.getUserId().equals(userId)) {
            throw new RuntimeException("Access Denied: not the owner of the review");
        }
        reviewRepository.delete(review);
    }
}
