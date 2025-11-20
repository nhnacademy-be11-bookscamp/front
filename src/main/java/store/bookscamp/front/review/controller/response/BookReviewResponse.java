package store.bookscamp.front.review.controller.response;

import java.time.LocalDateTime;
import java.util.List;

public record BookReviewResponse(

        Long reviewId,
        String username,
        String content,
        Integer score,
        LocalDateTime createdAt,
        List<String> imageUrls
) {}
