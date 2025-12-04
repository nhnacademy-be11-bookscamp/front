package store.bookscamp.front.payment.dto;

public record PaymentCancelRequest(
        Long orderId,
        String cancelReason
) {
}