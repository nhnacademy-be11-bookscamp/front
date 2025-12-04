package store.bookscamp.front.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.bookscamp.front.payment.dto.PaymentConfirmRequest;
import store.bookscamp.front.payment.dto.PaymentConfirmResponse;
import store.bookscamp.front.payment.feign.PaymentFeignClient;

@Slf4j
@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentRestController {

    private final PaymentFeignClient paymentFeignClient;

    @PostMapping("/confirm")
    public ResponseEntity<PaymentConfirmResponse> confirmPayment(@RequestBody PaymentConfirmRequest request) {
        log.info("=== 결제 승인 요청 시작 ===");
        log.info("요청 데이터: {}", request);

        ResponseEntity<PaymentConfirmResponse> response = paymentFeignClient.confirmPayment(request);

        log.info("결제 승인 응답 상태: {}", response.getStatusCode());
        log.info("결제 승인 응답 데이터: {}", response.getBody());
        log.info("=== 결제 승인 요청 완료 ===");

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}
