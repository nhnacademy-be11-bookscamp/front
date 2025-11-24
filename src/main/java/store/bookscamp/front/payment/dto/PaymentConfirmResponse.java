package store.bookscamp.front.payment.dto;

import java.time.LocalDateTime;

public record PaymentConfirmResponse(
        Long paymentId,
        Long orderId,
        Integer paidAmount,
        LocalDateTime paidAt
) {}