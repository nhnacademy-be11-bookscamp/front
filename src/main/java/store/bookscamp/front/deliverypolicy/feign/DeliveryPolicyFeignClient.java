package store.bookscamp.front.deliverypolicy.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import store.bookscamp.front.deliverypolicy.controller.request.DeliveryPolicyCreateRequest;
import store.bookscamp.front.deliverypolicy.controller.request.DeliveryPolicyUpdateRequest;
import store.bookscamp.front.deliverypolicy.controller.response.DeliveryPolicyResponse;

@FeignClient(name = "deliveryPolicyClient", url = "${gateway.base-url}")
public interface DeliveryPolicyFeignClient {

    // 배송비 정책 생성 (POST)
    @PostMapping("/api-server/admin/delivery-policy")
    DeliveryPolicyResponse createDeliveryPolicy(
            @RequestBody DeliveryPolicyCreateRequest createRequest);

    @GetMapping("/api-server/admin/delivery-policy")
    DeliveryPolicyResponse getDeliveryPolicy();

    // 수정
    @PutMapping("/api-server/admin/delivery-policy")
    DeliveryPolicyResponse updateDeliveryPolicy(
            @RequestBody DeliveryPolicyUpdateRequest updateRequest);
}
