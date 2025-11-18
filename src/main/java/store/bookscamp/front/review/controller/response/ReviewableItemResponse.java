package store.bookscamp.front.review.controller.response;

public record ReviewableItemResponse (
    Long orderItemId,
    Long bookId,
    String title,
    String thumbnailUrl
) {}
