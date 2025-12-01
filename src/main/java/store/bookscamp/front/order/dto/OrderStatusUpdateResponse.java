package store.bookscamp.front.order.dto;

import java.time.LocalDateTime;

public record OrderStatusUpdateResponse(
        Long orderId,
        String orderNumber,
        String currentStatus,
        LocalDateTime updatedAt
) {
}