package store.bookscamp.front.payment.dto;

public record PaymentConfirmRequest(
        String paymentKey,
        String orderNumber,
        Integer amount
) {}