package app.web;

import app.model.Review;
import app.service.ReviewService;
import app.web.dto.CreateReviewRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Autowired
    public ReviewController(ReviewService reviewService) {

        this.reviewService = reviewService;

    }

    @GetMapping("/listing/{listingId}")
    public List<Review> getReviews(@PathVariable UUID listingId) {

        return reviewService.getReviewsByListing(listingId);

    }

    @PostMapping
    public Review createReview(@RequestBody CreateReviewRequest request) {

        return reviewService.createReview(request.getListingId(), request.getUserId(), request.getContent());

    }

    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable UUID reviewId, @RequestParam UUID userId, @RequestParam boolean isAdmin) {

        reviewService.deleteReview(reviewId, userId, isAdmin);

    }
}

