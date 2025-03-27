package app.review.client.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ReviewDto {
    private UUID id;
    private UUID listingId;
    private UUID userId;
    private String content;
    private LocalDateTime createdAt;
    private String username;

}
