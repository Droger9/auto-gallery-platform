package app.web;

import app.model.Review;
import app.service.impl.ReviewServiceImpl;
import app.web.dto.CreateReviewRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;


@WebMvcTest(ReviewController.class)
public class ReviewControllerApiTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ReviewServiceImpl reviewService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testGetReviewsByListing() throws Exception {
        UUID listingId = UUID.randomUUID();

        Review review = new Review();
        review.setId(UUID.randomUUID());
        review.setListingId(listingId);
        review.setUserId(UUID.randomUUID());
        review.setContent("Great car!");
        review.setCreatedAt(LocalDateTime.now());

        List<Review> reviewList = Arrays.asList(review);
        when(reviewService.getReviewsByListing(listingId)).thenReturn(reviewList);

        mockMvc.perform(get("/api/v1/reviews/listing/{listingId}", listingId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].content").isNotEmpty());

        verify(reviewService, times(1)).getReviewsByListing(listingId);
    }

    @Test
    public void testCreateReview() throws Exception {
        UUID listingId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        CreateReviewRequest request = new CreateReviewRequest();
        request.setListingId(listingId);
        request.setUserId(userId);
        request.setContent("Awesome review content");

        Review review = new Review();
        review.setId(UUID.randomUUID());
        review.setListingId(listingId);
        review.setUserId(userId);
        review.setContent("Awesome review content");
        review.setCreatedAt(LocalDateTime.now());

        when(reviewService.createReview(eq(listingId), eq(userId), eq("Awesome review content")))
                .thenReturn(review);

        String jsonRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/v1/reviews")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content").isNotEmpty());

        verify(reviewService, times(1))
                .createReview(eq(listingId), eq(userId), eq("Awesome review content"));
    }

    @Test
    public void testDeleteReview() throws Exception {
        UUID reviewId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        boolean isAdmin = false;

        doNothing().when(reviewService).deleteReview(eq(reviewId), eq(userId), eq(isAdmin));

        mockMvc.perform(delete("/api/v1/reviews/{reviewId}", reviewId)
                        .param("userId", userId.toString())
                        .param("isAdmin", String.valueOf(isAdmin)))
                .andExpect(status().isOk());

        verify(reviewService, times(1)).deleteReview(eq(reviewId), eq(userId), eq(isAdmin));
    }
}
