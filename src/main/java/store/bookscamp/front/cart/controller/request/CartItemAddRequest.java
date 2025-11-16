package store.bookscamp.front.cart.controller.request;

import jakarta.validation.constraints.NotNull;

public record CartItemAddRequest(

        @NotNull
        Long bookId,

        @NotNull
        Integer quantity
) {
}
