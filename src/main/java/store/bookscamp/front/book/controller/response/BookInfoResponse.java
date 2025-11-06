package store.bookscamp.front.book.controller.response;

import store.bookscamp.front.book.BookStatus;

import java.time.LocalDate;
import java.util.List;

public record BookInfoResponse(
        Long id,
        String title,
        String explanation,
        String content,
        String publisher,
        LocalDate publishDate,
        String contributors,
        String isbn,
        BookStatus status,
        boolean packable,
        Integer regularPrice,
        Integer salePrice,
        Integer stock,
        long viewCount,
        Long categoryId,
        List<Long> tagIds,
        List<String> imageUrls
) {}
