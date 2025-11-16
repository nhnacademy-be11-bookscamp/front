package store.bookscamp.front.order.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import store.bookscamp.front.common.config.FeignConfig;
import store.bookscamp.front.order.dto.OrderCreateRequest;
import store.bookscamp.front.order.dto.OrderCreateResponse;
import store.bookscamp.front.order.dto.OrderPrepareRequest;
import store.bookscamp.front.order.dto.OrderPrepareResponse;

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
}