package store.bookscamp.front.payment.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import store.bookscamp.front.common.config.FeignConfig;
import store.bookscamp.front.payment.dto.PaymentConfirmRequest;
import store.bookscamp.front.payment.dto.PaymentConfirmResponse;

@FeignClient(
        name = "api-through-gateway-payment",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface PaymentFeignClient {

    @PostMapping(value = "/api-server/payments/confirm", produces = "application/json")
    ResponseEntity<PaymentConfirmResponse> confirmPayment(@RequestBody PaymentConfirmRequest request);
}