package store.bookscamp.front.deliverypolicy.controller.request;

import jakarta.validation.constraints.PositiveOrZero;

public record DeliveryPolicyUpdateRequest(
        @PositiveOrZero
        Integer freeDeliveryThreshold,

        @PositiveOrZero
        Integer baseDeliveryFee
) {
}