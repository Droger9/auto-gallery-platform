package app.review.client;

import app.review.client.dto.CreateReviewRequestDto;
import app.review.client.dto.ReviewDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@FeignClient(
        name = "car-reviews-microservice",
        url = "${reviews.service.url}"
)
public interface ReviewClient {

    @GetMapping("/listing/{listingId}")
    List<ReviewDto> getReviews(@PathVariable("listingId") UUID listingId);

    @PostMapping
    ReviewDto createReview(@RequestBody CreateReviewRequestDto requestDto);

    @DeleteMapping("/{reviewId}")
    void deleteReview(@PathVariable("reviewId") UUID reviewId, @RequestParam("userId") UUID userId, @RequestParam("isAdmin") boolean isAdmin);
}

