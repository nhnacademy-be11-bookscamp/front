package store.bookscamp.front.order.dto;

import java.util.List;

public record OrderPrepareRequest(
        List<OrderItemRequest> items,
        String orderType
) {
    public record OrderItemRequest(
            Long bookId,
            Integer quantity,
            Long packagingId
    ) {}
}