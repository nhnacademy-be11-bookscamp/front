package store.bookscamp.front.payment.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import store.bookscamp.front.payment.dto.PaymentConfirmRequest;
import store.bookscamp.front.payment.dto.PaymentConfirmResponse;
import store.bookscamp.front.payment.feign.PaymentFeignClient;

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
        model.addAttribute("errorCode", code);
        model.addAttribute("errorMessage", message);
        return "payment/fail";
    }

    @PostMapping("/confirm")
    @ResponseBody
    public ResponseEntity<PaymentConfirmResponse> confirmPayment(@RequestBody PaymentConfirmRequest request) {
        return paymentFeignClient.confirmPayment(request);
    }
}