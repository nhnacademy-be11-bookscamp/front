package store.bookscamp.front.order.dto;

public record OrderReturnResponse(
        String orderNumber,
        String orderStatus,
        Long point
) {
}