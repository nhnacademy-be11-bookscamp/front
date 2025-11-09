package store.bookscamp.front.book.controller.response;

public record BookIndexResponse(

        Long id,
        String title,
        String publisher,
        String contributors,
        Integer regularPrice,
        Integer salePrice,
        String thumbnail
) {
}
