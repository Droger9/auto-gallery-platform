package app.web;

import app.model.Review;
import app.service.impl.ReviewServiceImpl;
import app.web.dto.CreateReviewRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/reviews")
public class ReviewController {

    private final ReviewServiceImpl reviewService;

    @Autowired
    public ReviewController(ReviewServiceImpl reviewService) {

        this.reviewService = reviewService;

    }

    @GetMapping("/listing/{listingId}")
    public List<Review> getReviews(@PathVariable UUID listingId) {

        return reviewService.getReviewsByListing(listingId);

    }

    @PostMapping
    public ResponseEntity<Review> createReview(@RequestBody CreateReviewRequest request) {
        Review review = reviewService.createReview(request.getListingId(), request.getUserId(), request.getContent());
        return ResponseEntity.status(HttpStatus.CREATED).body(review);
    }


    @DeleteMapping("/{reviewId}")
    public void deleteReview(@PathVariable UUID reviewId, @RequestParam UUID userId, @RequestParam boolean isAdmin) {

        reviewService.deleteReview(reviewId, userId, isAdmin);

    }
}

