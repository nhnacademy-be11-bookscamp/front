package store.bookscamp.front.payment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

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
}