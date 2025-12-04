package store.bookscamp.front.payment.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import store.bookscamp.front.payment.dto.PaymentCancelRequest;
import store.bookscamp.front.payment.dto.PaymentConfirmRequest;
import store.bookscamp.front.payment.dto.PaymentConfirmResponse;
import store.bookscamp.front.payment.feign.PaymentFeignClient;

@Slf4j
@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentFeignClient paymentFeignClient;

    @GetMapping("/success")
    public String paymentSuccess(
            @RequestParam String paymentKey,
            @RequestParam String orderId,
            @RequestParam Integer amount,
            Model model
    ) {
        log.info("=== 결제 성공 페이지 요청 ===");
        log.info("결제 키: {}", paymentKey);
        log.info("주문 번호: {}", orderId);
        log.info("결제 금액: {}", amount);

        model.addAttribute("paymentKey", paymentKey);
        model.addAttribute("orderNumber", orderId);
        model.addAttribute("amount", amount);
        return "payment/success";
    }

    @GetMapping("/fail")
    public String paymentFail(
            @RequestParam String code,
            @RequestParam String message,
            Model model
    ) {
        log.error("=== 결제 실패 페이지 요청 ===");
        log.error("에러 코드: {}", code);
        log.error("에러 메시지: {}", message);

        model.addAttribute("errorCode", code);
        model.addAttribute("errorMessage", message);
        return "payment/fail";
    }

    @PostMapping("/confirm")
    @ResponseBody
    public ResponseEntity<PaymentConfirmResponse> confirmPayment(@RequestBody PaymentConfirmRequest request) {
        log.info("=== 결제 승인 요청 시작 ===");
        log.info("요청 데이터: {}", request);

        ResponseEntity<PaymentConfirmResponse> response = paymentFeignClient.confirmPayment(request);

        log.info("결제 승인 응답 상태: {}", response.getStatusCode());
        log.info("결제 승인 응답 데이터: {}", response.getBody());
        log.info("=== 결제 승인 요청 완료 ===");

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PostMapping("/cancel")
    @ResponseBody
    public ResponseEntity<String> cancelPayment(@RequestBody PaymentCancelRequest request) {
        log.info("결제 취소 요청 - orderId: {}, cancelReason: {}", request.orderId(), request.cancelReason());

        try {
            ResponseEntity<Void> response = paymentFeignClient.cancelPayment(request);

            log.info("결제 취소 완료 - orderId: {}, statusCode: {}", request.orderId(), response.getStatusCode());

            return ResponseEntity.status(response.getStatusCode()).body("결제가 취소되었습니다.");
        } catch (FeignException e) {
            log.error("결제 취소 실패 - orderId: {}, statusCode: {}, error: {}",
                    request.orderId(), e.status(), e.contentUTF8());

            return ResponseEntity
                    .status(e.status())
                    .body(e.contentUTF8());
        }
    }
}