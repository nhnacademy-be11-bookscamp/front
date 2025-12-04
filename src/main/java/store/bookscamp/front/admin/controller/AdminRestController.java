package store.bookscamp.front.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import store.bookscamp.front.order.dto.OrderStatusUpdateRequest;
import store.bookscamp.front.order.dto.OrderStatusUpdateResponse;
import store.bookscamp.front.order.feign.OrderFeignClient;

@Slf4j
@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminRestController {

    private final OrderFeignClient orderFeignClient;

    @PostMapping("/orders/{orderId}/status")
    public ResponseEntity<OrderStatusUpdateResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusUpdateRequest request
    ) {
        log.info("주문 상태 변경 요청 - orderId: {}, newStatus: {}", orderId, request.orderStatus());

        ResponseEntity<OrderStatusUpdateResponse> response = orderFeignClient.updateOrderStatus(orderId, request);

        log.info("주문 상태 변경 완료 - orderId: {}, statusCode: {}, response: {}",
                orderId, response.getStatusCode(), response.getBody());

        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }
}
