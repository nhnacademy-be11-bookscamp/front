package store.bookscamp.front.book.controller.response;

import java.time.LocalDate;
import java.util.List;

public record BookGetResponse (

        Long id,
        String title,
        String contributors,
        String publisher,
        String isbn,
        LocalDate publishDate,
        Integer regularPrice,
        Integer salePrice,
        Integer stock,
        boolean packable,
        String content,
        String explanation,
        List<String> imgUrls,
        List<Long> tagIds,
        List<Long> categoryIds
) {}
