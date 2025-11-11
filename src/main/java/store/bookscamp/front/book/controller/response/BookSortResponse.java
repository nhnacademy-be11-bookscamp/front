package store.bookscamp.front.book.controller.response;

import java.time.LocalDate;

public record BookSortResponse(

        Long id,
        String title,
        String publisher,
        LocalDate publishDate,
        String contributors,
        boolean packable,
        Integer regularPrice,
        Integer salePrice,
        Integer stock,
        String thumbnailUrl,
        long viewCount
) {
}
