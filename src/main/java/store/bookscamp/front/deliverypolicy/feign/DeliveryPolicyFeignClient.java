package store.bookscamp.front.deliverypolicy.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.deliverypolicy.controller.DeliveryPolicyController;
import store.bookscamp.front.deliverypolicy.controller.request.DeliveryPolicyCreateRequest;
import store.bookscamp.front.deliverypolicy.controller.request.DeliveryPolicyUpdateRequest;
import store.bookscamp.front.deliverypolicy.controller.response.DeliveryFeeResponse;
import store.bookscamp.front.deliverypolicy.controller.response.DeliveryPolicyResponse;

@FeignClient(name = "deliveryPolicyClient", url = "${gateway.base-url}") // api-server
public interface DeliveryPolicyFeignClient {

    // 관리자 전용 API
    @PostMapping("/api-server/admin/delivery-policies")
    DeliveryPolicyResponse createDeliveryPolicy(@RequestBody DeliveryPolicyCreateRequest createRequest);

    @GetMapping("/api-server/admin/delivery-policies")
    List<DeliveryPolicyResponse> getAllDeliveryPolicies();

    @PutMapping("/api-server/admin/delivery-policies/{id}")
    DeliveryPolicyResponse updateDeliveryPolicy(
            @PathVariable("id") Long id,
            @RequestBody DeliveryPolicyUpdateRequest updateRequest);

    @DeleteMapping("/api-server/admin/delivery-policies/{id}")
    void delete(@PathVariable("id") Long id);


    // 사용자 전용 API
    @GetMapping("/api-server/delivery-policies/current")
    DeliveryPolicyResponse getCurrentDeliveryPolicy();

    @GetMapping("/api-server/delivery-policies/is-free")
    boolean isFreeByTotal(@RequestParam("orderTotal") int orderTotal);

    // 총액 기준 배송비 계산
    @GetMapping("/api-server/delivery-policies/fee")
    DeliveryFeeResponse calculateFee(@RequestParam("orderTotal") int orderTotal);
}
