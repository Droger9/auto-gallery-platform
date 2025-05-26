package app.review.service.impl;

import app.model.Role;
import app.model.User;
import app.review.client.ReviewClient;
import app.review.client.dto.CreateReviewRequestDto;
import app.review.client.dto.ReviewDto;
import app.review.service.ReviewService;
import app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ReviewServiceImpl implements ReviewService {

    private final ReviewClient reviewClient;
    private final UserService userService;

    @Autowired
    public ReviewServiceImpl(ReviewClient reviewClient, UserService userService) {
        this.reviewClient = reviewClient;
        this.userService = userService;
    }

    @Override
    public CreateReviewRequestDto getCreateReviewRequestDto(UUID id, String content, User user) {
        CreateReviewRequestDto requestDto = new CreateReviewRequestDto();
        requestDto.setListingId(id);
        requestDto.setUserId(user.getId());
        requestDto.setContent(content);
        return requestDto;
    }

    @Override
    public List<ReviewDto> getReviewDtos(UUID id) {
        return reviewClient.getReviews(id);
    }

    @Override
    public void createReview(CreateReviewRequestDto requestDto) {
        reviewClient.createReview(requestDto);
    }

    @Override
    public void deleteReview(UUID reviewId, User user) {
        boolean isAdmin = user.getRole() == Role.ADMIN;
        reviewClient.deleteReview(reviewId, user.getId(), isAdmin);
    }

    @Override
    public void addUsernameToDto(List<ReviewDto> reviews) {
        for (ReviewDto review : reviews) {
            User user = userService.getById(review.getUserId());
            review.setUsername(user != null ? user.getUsername() : "Unknown User");
        }
    }
}
