package store.bookscamp.front.book.controller.dto.response;

import java.time.LocalDate;

public record BookInfoResponse(

        Long id,
        String title,
        String explanation,
        String content,
        String publisher,
        LocalDate publishDate,
        boolean packable,
        Integer regularPrice,
        Integer salePrice,
        Integer stock,
        long viewCount
) {
}
