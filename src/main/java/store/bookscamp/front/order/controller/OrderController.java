package store.bookscamp.front.order.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import store.bookscamp.front.order.dto.OrderCreateRequest;
import store.bookscamp.front.order.dto.OrderCreateResponse;
import store.bookscamp.front.order.dto.OrderPrepareRequest;
import store.bookscamp.front.order.dto.OrderPrepareResponse;
import store.bookscamp.front.order.feign.OrderFeignClient;

import java.util.Arrays;
import java.util.List;

@Controller
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderFeignClient orderFeignClient;

    @PostMapping("/prepare/direct")
    public String prepareDirectOrder(
            @RequestParam Long bookId,
            @RequestParam(defaultValue = "1") Integer quantity,
            HttpServletRequest request,
            Model model
    ) {
        boolean isMember = isAuthenticatedMember(request);

        OrderPrepareRequest.OrderItemRequest item =
                new OrderPrepareRequest.OrderItemRequest(bookId, quantity);
        OrderPrepareRequest prepareRequest = new OrderPrepareRequest(List.of(item));

        ResponseEntity<OrderPrepareResponse> response = orderFeignClient.prepareOrder(prepareRequest);
        OrderPrepareResponse orderData = response.getBody();

        model.addAttribute("orderData", orderData);
        model.addAttribute("isMember", isMember);

        return "order/order-prepare";
    }

    @PostMapping("/prepare/cart")
    public String prepareCartOrder(
            @RequestBody OrderPrepareRequest request,
            HttpServletRequest httpRequest,
            Model model
    ) {
        boolean isMember = isAuthenticatedMember(httpRequest);

        ResponseEntity<OrderPrepareResponse> response = orderFeignClient.prepareOrder(request);
        OrderPrepareResponse orderData = response.getBody();

        model.addAttribute("orderData", orderData);
        model.addAttribute("isMember", isMember);

        return "order/order-prepare";
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<OrderCreateResponse> createOrder(@RequestBody OrderCreateRequest request) {
        return orderFeignClient.createOrder(request);
    }

    private boolean isAuthenticatedMember(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }

        return Arrays.stream(cookies)
                .anyMatch(cookie -> "Authorization".equals(cookie.getName())
                        && cookie.getValue() != null
                        && !cookie.getValue().isEmpty());
    }
}