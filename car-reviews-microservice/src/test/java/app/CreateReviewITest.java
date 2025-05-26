package app;

import app.model.Review;
import app.repository.ReviewRepository;
import app.service.impl.ReviewServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest
public class CreateReviewITest {

    @Autowired
    private ReviewServiceImpl reviewService;

    @Autowired
    private ReviewRepository reviewRepository;

    @Test
    public void testCreateReviewIntegration() {
        // Given
        UUID listingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        String content = "Excellent car and service!";

        // When
        Review createdReview = reviewService.createReview(listingId, userId, content);

        // Then
        assertNotNull(createdReview, "Created review should not be null");
        assertEquals(listingId, createdReview.getListingId(), "Listing ID should match");
        assertEquals(userId, createdReview.getUserId(), "User ID should match");
        assertEquals(content, createdReview.getContent(), "Content should match");
        assertNotNull(createdReview.getCreatedAt(), "Creation date should be set");

        Optional<Review> optionalReview = Optional.ofNullable(reviewRepository.findById(createdReview.getId()));
        assertTrue(optionalReview.isPresent(), "Review should be found in the repository");
        Review fetchedReview = optionalReview.get();
        assertEquals(content, fetchedReview.getContent(), "Fetched review's content should match");
    }
}
