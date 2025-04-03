package app.service;

import app.model.Review;
import app.repository.ReviewRepository;
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
public class ReviewServiceUTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Review testReview;
    private UUID listingId;
    private UUID ownerUserId;
    private UUID otherUserId;
    private UUID reviewId;

    @BeforeEach
    public void setUp() {
        listingId = UUID.randomUUID();
        ownerUserId = UUID.randomUUID();
        otherUserId = UUID.randomUUID();
        reviewId = UUID.randomUUID();

        testReview = new Review();
        testReview.setId(reviewId);
        testReview.setListingId(listingId);
        testReview.setUserId(ownerUserId);
        testReview.setContent("Great car!");
        testReview.setCreatedAt(LocalDateTime.now());
    }

    @Test
    public void testGetReviewsByListing() {
        List<Review> reviews = Arrays.asList(testReview);
        when(reviewRepository.findAllByListingId(listingId)).thenReturn(reviews);

        List<Review> result = reviewService.getReviewsByListing(listingId);
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testReview, result.get(0));
    }

    @Test
    public void testCreateReview() {
        when(reviewRepository.save(any(Review.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Review result = reviewService.createReview(listingId, ownerUserId, "Awesome car!");
        assertNotNull(result);
        assertEquals(listingId, result.getListingId());
        assertEquals(ownerUserId, result.getUserId());
        assertEquals("Awesome car!", result.getContent());
        assertNotNull(result.getCreatedAt());
    }

    @Test
    public void testDeleteReview_OwnerCanDelete() {

        when(reviewRepository.findById(reviewId)).thenReturn(testReview);

        assertDoesNotThrow(() -> reviewService.deleteReview(reviewId, ownerUserId, false));
        verify(reviewRepository, times(1)).delete(testReview);
    }

    @Test
    public void testDeleteReview_AdminCanDelete() {

        when(reviewRepository.findById(reviewId)).thenReturn(testReview);

        assertDoesNotThrow(() -> reviewService.deleteReview(reviewId, otherUserId, true));
        verify(reviewRepository, times(1)).delete(testReview);
    }

    @Test
    public void testDeleteReview_NotOwnerAndNotAdmin() {

        when(reviewRepository.findById(reviewId)).thenReturn(testReview);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> {
            reviewService.deleteReview(reviewId, otherUserId, false);
        });
        assertEquals("Access Denied: not the owner of the review", ex.getMessage());
        verify(reviewRepository, never()).delete(any(Review.class));
    }
}
