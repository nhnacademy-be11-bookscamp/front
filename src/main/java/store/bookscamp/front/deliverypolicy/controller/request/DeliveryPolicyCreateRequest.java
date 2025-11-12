package store.bookscamp.front.deliverypolicy.controller.request;

import jakarta.validation.constraints.NotNull;

public record DeliveryPolicyCreateRequest(
        @NotNull
        int freeDeliveryThreshold,  // 무료배송 기준 금액 (원)

        @NotNull
        int baseDeliveryFee  // 기본 배송비 (원)
) {
}
