package store.bookscamp.front.deliverypolicy.controller.request;

import jakarta.validation.constraints.NotNull;

public record DeliveryPolicyUpdateRequest(
        @NotNull
        Integer freeDeliveryThreshold,  // 무료배송 기준 금액 (수정 선택적)

        @NotNull
        Integer baseDeliveryFee  // 기본 배송비 (수정 선택적)
) {
}