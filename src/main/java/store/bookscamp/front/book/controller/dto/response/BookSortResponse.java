package store.bookscamp.front.book.controller.dto.response;

import java.time.LocalDate;

public record BookSortResponse(

        Long id,
        String title,
        String explanation,
        String content,
        String publisher,
        LocalDate publishDate,
//        Contributor contributor,
//        BookStatus status,
        boolean packable,
        Integer regularPrice,
        Integer salePrice,
        Integer stock,
        long viewCount
) {
}
