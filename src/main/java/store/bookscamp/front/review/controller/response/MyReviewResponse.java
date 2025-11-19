package store.bookscamp.front.review.controller.response;

import java.time.LocalDateTime;
import java.util.List;

public record MyReviewResponse(
        Long reviewId,
        Long bookId,
        String bookTitle,
        String thumbnailUrl,
        Integer score,
        String content,
        List<String> imageUrls,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
