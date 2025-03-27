package app.review.client.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class CreateReviewRequestDto {
    private UUID listingId;
    private UUID userId;
    private String content;
}
