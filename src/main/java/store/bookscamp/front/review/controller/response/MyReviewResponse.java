package store.bookscamp.front.review.controller.response;

import java.util.List;

public record MyReviewResponse(
        Long reviewId,
        Long bookId,
        String bookTitle,
        String thumbnailUrl,
        int score,
        String content,
        List<String> imageUrls,
        String createdAt,
        String updatedAt
) {}
