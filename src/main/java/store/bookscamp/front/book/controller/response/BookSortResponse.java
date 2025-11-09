<<<<<<<< HEAD:src/main/java/store/bookscamp/front/book/controller/dto/response/BookSortResponse.java
package store.bookscamp.front.book.controller.dto.response;
========
package store.bookscamp.front.book.controller.response;
>>>>>>>> 1eecf3d160826700fa0992d1e27dfa8341a046f6:src/main/java/store/bookscamp/front/book/controller/response/BookSortResponse.java

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
