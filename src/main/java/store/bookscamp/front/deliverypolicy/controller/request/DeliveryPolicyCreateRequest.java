package store.bookscamp.front.deliverypolicy.controller.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryPolicyCreateRequest {
    @NotNull(message = "무료 배송 기준 금액을 입력해주세요.")
    @Min(value = 0, message = "무료 배송 기준 금액은 0원 이상이어야 합니다.")
    private Integer freeDeliveryThreshold;

    @NotNull(message = "기본 배송비를 입력해주세요.")
    @Min(value = 0, message = "기본 배송비는 0원 이상이어야 합니다.")
    private Integer baseDeliveryFee;
}
