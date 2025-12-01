package store.bookscamp.front.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.common.config.FeignConfig;
import store.bookscamp.front.order.dto.NonMemberOrderRequest;
import store.bookscamp.front.order.dto.OrderCreateRequest;
import store.bookscamp.front.order.dto.OrderCreateResponse;
import store.bookscamp.front.order.dto.OrderDetailResponse;
import store.bookscamp.front.order.dto.OrderListResponse;
import store.bookscamp.front.order.dto.OrderPrepareRequest;
import store.bookscamp.front.order.dto.OrderPrepareResponse;
import store.bookscamp.front.order.dto.OrderStatusUpdateRequest;
import store.bookscamp.front.order.dto.OrderStatusUpdateResponse;
import store.bookscamp.front.order.dto.PageResponse;

@FeignClient(
        name = "api-through-gateway-order",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface OrderFeignClient {

    @PostMapping(value = "/api-server/orders/prepare", produces = "application/json")
    ResponseEntity<OrderPrepareResponse> prepareOrder(@RequestBody OrderPrepareRequest request);

    @PostMapping(value = "/api-server/orders", produces = "application/json")
    ResponseEntity<OrderCreateResponse> createOrder(@RequestBody OrderCreateRequest request);

    /**
     * 주문 내역 조회
     */
    @GetMapping(value = "/api-server/orders/list", produces = "application/json")
    ResponseEntity<PageResponse<OrderListResponse>> getOrderList(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );

    /**
     * 상세 주문 내역 조회
     */
    @GetMapping("/api-server/orders/{orderId}")
    ResponseEntity<OrderDetailResponse> getOrderDetail(
            @PathVariable Long orderId);

    /**
     * 비회원 주문 상세 조회
     * 주문번호 + 비밀번호
     */
    @PostMapping("/api-server/orders/non-member/{orderNumber}")
    ResponseEntity<OrderDetailResponse> getNonMemberOrderDetail(
            @PathVariable("orderNumber") String orderNumber,
            @RequestBody NonMemberOrderRequest request
    );

    @GetMapping(value = "/api-server/admin/orders", produces = "application/json")
    ResponseEntity<PageResponse<OrderListResponse>> getAdminOrderList(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "size", defaultValue = "20") int size,
            @RequestParam(value = "sort", defaultValue = "createdAt,desc") String sort
    );

    @GetMapping("/api-server/admin/orders/{orderId}")
    ResponseEntity<OrderDetailResponse> getAdminOrderDetail(
            @PathVariable Long orderId
    );

    @PutMapping(value = "/api-server/admin/orders/{orderId}/status", produces = "application/json")
    ResponseEntity<OrderStatusUpdateResponse> updateOrderStatus(
            @PathVariable Long orderId,
            @RequestBody OrderStatusUpdateRequest request
    );
}