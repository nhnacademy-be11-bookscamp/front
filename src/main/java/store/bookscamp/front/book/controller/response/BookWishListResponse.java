package store.bookscamp.front.book.controller.response;

import java.time.LocalDate;
import store.bookscamp.front.book.status.BookStatus;

public record BookWishListResponse(

        Long id,
        String title,
        String publisher,
        LocalDate publishDate,
        String contributors,
        boolean packable,
        Integer regularPrice,
        Integer salePrice,
        BookStatus status,
        String thumbnailUrl
) {
}
