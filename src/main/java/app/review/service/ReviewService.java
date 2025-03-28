package app.review.service;

import app.model.Role;
import app.model.User;
import app.review.client.ReviewClient;
import app.review.client.dto.CreateReviewRequestDto;
import app.review.client.dto.ReviewDto;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewService {

    private final ReviewClient reviewClient;
    private final UserService userService;

    @Autowired
    public ReviewService(ReviewClient reviewClient, UserService userService) {
        this.reviewClient = reviewClient;
        this.userService = userService;
    }

    public CreateReviewRequestDto getCreateReviewRequestDto(UUID id, String content, User user) {

        CreateReviewRequestDto requestDto = new CreateReviewRequestDto();
        requestDto.setListingId(id);
        requestDto.setUserId(user.getId());
        requestDto.setContent(content);

        return requestDto;
    }

    public List<ReviewDto> getReviewDtos(UUID id) {

        List<ReviewDto> reviews = reviewClient.getReviews(id);
        return reviews;

    }

    public void createReview(CreateReviewRequestDto requestDto) {
        reviewClient.createReview(requestDto);
    }

    public void deleteReview(UUID reviewId, User user) {

        boolean isAdmin = (user.getRole() == Role.ADMIN);
        reviewClient.deleteReview(reviewId, user.getId(), isAdmin);

    }

    public void addUsernameToDto(List<ReviewDto> reviews) {

        for (ReviewDto review : reviews) {

            User user = userService.getById(review.getUserId());

            if (user != null) {
                review.setUsername(user.getUsername());
            } else {
                review.setUsername("Unknown User");
            }
        }
    }
}
