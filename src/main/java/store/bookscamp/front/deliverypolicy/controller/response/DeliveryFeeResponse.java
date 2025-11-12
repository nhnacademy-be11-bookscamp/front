package store.bookscamp.front.deliverypolicy.controller.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DeliveryFeeResponse {

    private int orderTotal;
    private int deliveryFee;
    private boolean free;

}
