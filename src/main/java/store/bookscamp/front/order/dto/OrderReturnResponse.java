package store.bookscamp.front.order.dto;

public record OrderReturnResponse(
        String orderNumber,
        Long point
) {
}