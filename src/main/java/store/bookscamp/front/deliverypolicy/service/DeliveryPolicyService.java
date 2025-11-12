package store.bookscamp.front.deliverypolicy.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import store.bookscamp.front.deliverypolicy.controller.request.DeliveryPolicyCreateRequest;
import store.bookscamp.front.deliverypolicy.controller.request.DeliveryPolicyUpdateRequest;
import store.bookscamp.front.deliverypolicy.controller.response.DeliveryFeeResponse;
import store.bookscamp.front.deliverypolicy.controller.response.DeliveryPolicyResponse;
import store.bookscamp.front.deliverypolicy.feign.DeliveryPolicyFeignClient;

@Service
@RequiredArgsConstructor
public class DeliveryPolicyService {

    private final DeliveryPolicyFeignClient deliveryPolicyFeignClient;

    // 모든 배송비 정책 조회
    public List<DeliveryPolicyResponse> getAllDeliveryPolicies() {
        return deliveryPolicyFeignClient.getAllDeliveryPolicies();
    }

    // 현재 배송비 정책 조회
    public DeliveryPolicyResponse getCurrentDeliveryPolicy() {
        return deliveryPolicyFeignClient.getCurrentDeliveryPolicy();
    }

    // 총액 기준 무료배송 여부 판단 (사용자도 접근 가능)
    public boolean isFreeByTotal(int orderTotal) {
        return deliveryPolicyFeignClient.isFreeByTotal(orderTotal);
    }

    // 총액 기준 배송비 계산 (사용자도 접근 가능)
    public DeliveryFeeResponse calculateFee(int orderTotal) {
        return deliveryPolicyFeignClient.calculateFee(orderTotal);
    }

    // 관리자가 배송비 정책을 생성하는 메서드 (관리자 전용)
    public DeliveryPolicyResponse createDeliveryPolicy(DeliveryPolicyCreateRequest createRequest) {
        return deliveryPolicyFeignClient.createDeliveryPolicy(createRequest);
    }

    // 관리자가 배송비 정책을 수정하는 메서드 (관리자 전용)
    public DeliveryPolicyResponse updateDeliveryPolicy(Long id, DeliveryPolicyUpdateRequest updateRequest) {
        return deliveryPolicyFeignClient.updateDeliveryPolicy(id, updateRequest);
    }
}
