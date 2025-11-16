package store.bookscamp.front.order.dto;

public record OrderCreateResponse(
        Long orderId,
        Integer finalAmount
) {}