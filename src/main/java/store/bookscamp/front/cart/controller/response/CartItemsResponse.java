package store.bookscamp.front.cart.controller.response;

public record CartItemsResponse(
        Long cartItemId,
        Long bookId,
        String bookTitle,
        String thumbnailImageUrl,
        Integer quantity,
        Integer regularPrice,
        Integer salePrice,
        Integer totalPrice
) {
}
