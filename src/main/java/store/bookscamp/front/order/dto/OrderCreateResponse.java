package store.bookscamp.front.order.dto;

public record OrderCreateResponse(
        Long orderId,
        String orderNumber,
        Integer finalAmount
) {}