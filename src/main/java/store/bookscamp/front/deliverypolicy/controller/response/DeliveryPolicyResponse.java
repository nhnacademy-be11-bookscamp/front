package store.bookscamp.front.deliverypolicy.controller.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeliveryPolicyResponse {

    private Long id;
    private int feeDeliveryThreshold;
    private int baseDeliveryFee;
}
