package app.service;

import app.model.Role;
import app.model.User;
import app.review.client.ReviewClient;
import app.review.client.dto.CreateReviewRequestDto;
import app.review.client.dto.ReviewDto;
import app.review.service.impl.ReviewServiceImpl;
import app.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReviewServiceImplUTest {

    @Mock
    private ReviewClient reviewClient;

    @Mock
    private UserServiceImpl userServiceImpl;

    @InjectMocks
    private ReviewServiceImpl reviewService;

    private User testUser;
    private UUID testUserId;
    private UUID testListingId;
    private UUID testReviewId;

    @BeforeEach
    public void setUp() {
        testUserId = UUID.randomUUID();
        testUser = User.builder()
                .id(testUserId)
                .username("testuser")
                .role(Role.USER)
                .build();
        testListingId = UUID.randomUUID();
        testReviewId = UUID.randomUUID();
    }

    @Test
    public void testGetCreateReviewRequestDto() {
        String content = "Great car!";
        CreateReviewRequestDto dto = reviewService.getCreateReviewRequestDto(testListingId, content, testUser);

        assertNotNull(dto);
        assertEquals(testListingId, dto.getListingId());
        assertEquals(testUserId, dto.getUserId());
        assertEquals(content, dto.getContent());
    }

    @Test
    public void testGetReviewDtos() {
        ReviewDto review1 = new ReviewDto();
        review1.setId(testReviewId);
        review1.setListingId(testListingId);
        review1.setUserId(testUserId);
        review1.setContent("Nice ride");
        review1.setCreatedAt(LocalDateTime.now());

        List<ReviewDto> reviewList = Arrays.asList(review1);
        when(reviewClient.getReviews(testListingId)).thenReturn(reviewList);

        List<ReviewDto> result = reviewService.getReviewDtos(testListingId);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Nice ride", result.get(0).getContent());
        verify(reviewClient, times(1)).getReviews(testListingId);
    }

    @Test
    public void testCreateReview() {
        CreateReviewRequestDto requestDto = new CreateReviewRequestDto();
        requestDto.setListingId(testListingId);
        requestDto.setUserId(testUserId);
        requestDto.setContent("Awesome!");

        ReviewDto reviewDto = new ReviewDto();
        reviewDto.setId(UUID.randomUUID());
        reviewDto.setListingId(testListingId);
        reviewDto.setUserId(testUserId);
        reviewDto.setContent("Awesome!");
        reviewDto.setCreatedAt(LocalDateTime.now());

        when(reviewClient.createReview(requestDto)).thenReturn(reviewDto);

        reviewService.createReview(requestDto);
        verify(reviewClient, times(1)).createReview(requestDto);
    }

    @Test
    public void testDeleteReview_AsUser() {
        reviewService.deleteReview(testReviewId, testUser);
        verify(reviewClient, times(1)).deleteReview(testReviewId, testUserId, false);
    }

    @Test
    public void testDeleteReview_AsAdmin() {
        User adminUser = User.builder()
                .id(UUID.randomUUID())
                .username("admin")
                .role(Role.ADMIN)
                .build();
        reviewService.deleteReview(testReviewId, adminUser);
        verify(reviewClient, times(1)).deleteReview(testReviewId, adminUser.getId(), true);
    }

    @Test
    public void testAddUsernameToDto() {
        ReviewDto review = new ReviewDto();
        review.setId(testReviewId);
        review.setListingId(testListingId);
        review.setUserId(testUserId);
        review.setContent("Good car");
        review.setCreatedAt(LocalDateTime.now());

        List<ReviewDto> reviews = new ArrayList<>();
        reviews.add(review);

        when(userServiceImpl.getById(testUserId)).thenReturn(testUser);

        reviewService.addUsernameToDto(reviews);

        assertEquals("testuser", reviews.get(0).getUsername());
        verify(userServiceImpl, times(1)).getById(testUserId);
    }

    @Test
    public void testAddUsernameToDto_UserNotFound() {
        UUID missingUserId = UUID.randomUUID();
        ReviewDto review = new ReviewDto();
        review.setId(UUID.randomUUID());
        review.setListingId(testListingId);
        review.setUserId(missingUserId);
        review.setContent("Review with unknown user");
        review.setCreatedAt(LocalDateTime.now());

        List<ReviewDto> reviews = new ArrayList<>();
        reviews.add(review);

        lenient().when(userServiceImpl.getById(missingUserId)).thenReturn(null);

        reviewService.addUsernameToDto(reviews);

        assertEquals("Unknown User", reviews.get(0).getUsername());
    }

}
