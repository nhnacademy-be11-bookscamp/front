package store.bookscamp.front.order.controller;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.bookscamp.front.order.dto.OrderCreateRequest;
import store.bookscamp.front.order.dto.OrderCreateResponse;
import store.bookscamp.front.order.dto.OrderReturnRequest;
import store.bookscamp.front.order.dto.OrderReturnResponse;
import store.bookscamp.front.order.feign.OrderFeignClient;

@Slf4j
@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderRestController {

    private final OrderFeignClient orderFeignClient;

    @PostMapping
    public ResponseEntity<OrderCreateResponse> createOrder(@RequestBody OrderCreateRequest request) {
        log.info("=== 주문 생성 요청 시작 ===");
        log.info("요청 데이터: {}", request);

        ResponseEntity<OrderCreateResponse> response = orderFeignClient.createOrder(request);

        log.info("주문 생성 응답 상태: {}", response.getStatusCode());
        log.info("주문 생성 응답 데이터: {}", response.getBody());
        log.info("=== 주문 생성 요청 완료 ===");

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    @PostMapping("/{orderId}/return")
    public ResponseEntity<Object> returnOrder(
            @PathVariable Long orderId,
            @RequestBody OrderReturnRequest request
    ) {
        log.info("반품 신청 요청 - orderId: {}, returnType: {}", orderId, request.returnType());

        try {
            ResponseEntity<OrderReturnResponse> response = orderFeignClient.returnOrder(orderId, request);

            log.info("반품 신청 완료 - orderId: {}, statusCode: {}, response: {}",
                    orderId, response.getStatusCode(), response.getBody());

            return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
        } catch (FeignException e) {
            log.error("반품 신청 실패 - orderId: {}, statusCode: {}, error: {}",
                    orderId, e.status(), e.contentUTF8());

            return ResponseEntity
                    .status(e.status())
                    .body(e.contentUTF8());
        }
    }
}