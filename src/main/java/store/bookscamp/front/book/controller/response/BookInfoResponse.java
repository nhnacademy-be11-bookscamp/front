package store.bookscamp.front.book.controller.response;

import store.bookscamp.front.book.BookStatus;

import java.time.LocalDate;
import java.util.List;
import store.bookscamp.front.category.service.dto.CategoryDto;
import store.bookscamp.front.tag.service.dto.TagDto;

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
        List<CategoryDto> categoryList,
        List<TagDto> tagList,
        List<String> imageUrlList
) {}
