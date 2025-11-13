package store.bookscamp.front.deliverypolicy.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.bookscamp.front.deliverypolicy.controller.request.DeliveryPolicyUpdateRequest;
import store.bookscamp.front.deliverypolicy.controller.response.DeliveryPolicyResponse;
import store.bookscamp.front.deliverypolicy.feign.DeliveryPolicyFeignClient;

@Service
@RequiredArgsConstructor
public class DeliveryPolicyService {

    private final DeliveryPolicyFeignClient client;

    // 배송비 정책 조회
    public DeliveryPolicyResponse getDeliveryPolicy() {
        return client.getDeliveryPolicy();
    }

    // 관리자가 배송비 정책을 수정하는 메서드
    public DeliveryPolicyResponse updateDeliveryPolicy(DeliveryPolicyUpdateRequest request) {
        return client.updateDeliveryPolicy(request);
    }
}
