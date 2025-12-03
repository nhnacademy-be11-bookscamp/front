package store.bookscamp.front.deliverypolicy.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.bookscamp.front.deliverypolicy.controller.request.DeliveryPolicyCreateRequest;
import store.bookscamp.front.deliverypolicy.controller.request.DeliveryPolicyUpdateRequest;
import store.bookscamp.front.deliverypolicy.controller.response.DeliveryPolicyResponse;
import store.bookscamp.front.deliverypolicy.feign.DeliveryPolicyFeignClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class DeliveryPolicyServiceTest {

    @InjectMocks
    private DeliveryPolicyService deliveryPolicyService;

    @Mock
    private DeliveryPolicyFeignClient client;

    final Long testPolicyId = 1L;
    private final int testThreshold = 50000;
    private final int testFee = 3000;

    private DeliveryPolicyResponse createResponse() {
        return new DeliveryPolicyResponse(testPolicyId, testThreshold, testFee);
    }

    @Test
    @DisplayName("배송비 정책 생성 요청 시 Feign Client의 createDeliveryPolicy가 호출되고 응답을 반환한다")
    void createDeliveryPolicy_success() {
        DeliveryPolicyCreateRequest request = new DeliveryPolicyCreateRequest(testThreshold, testFee);
        DeliveryPolicyResponse mockResponse = createResponse();

        given(client.createDeliveryPolicy(any(DeliveryPolicyCreateRequest.class)))
                .willReturn(mockResponse);

        DeliveryPolicyResponse result = deliveryPolicyService.createDeliveryPolicy(request);

        assertThat(result).isEqualTo(mockResponse);
        verify(client).createDeliveryPolicy(request);
    }

    @Test
    @DisplayName("배송비 정책 조회 요청 시 Feign Client의 getDeliveryPolicy가 호출되고 응답을 반환한다")
    void getDeliveryPolicy_success() {
        DeliveryPolicyResponse mockResponse = createResponse();

        given(client.getDeliveryPolicy())
                .willReturn(mockResponse);

        DeliveryPolicyResponse result = deliveryPolicyService.getDeliveryPolicy();

        assertThat(result).isEqualTo(mockResponse);
        verify(client).getDeliveryPolicy();
    }

    @Test
    @DisplayName("배송비 정책 수정 요청 시 Feign Client의 updateDeliveryPolicy가 호출되고 응답을 반환한다")
    void updateDeliveryPolicy_success() {
        DeliveryPolicyUpdateRequest request = new DeliveryPolicyUpdateRequest(testThreshold, testFee);
        DeliveryPolicyResponse mockResponse = createResponse();

        given(client.updateDeliveryPolicy(any(DeliveryPolicyUpdateRequest.class)))
                .willReturn(mockResponse);

        DeliveryPolicyResponse result = deliveryPolicyService.updateDeliveryPolicy(request);

        assertThat(result).isEqualTo(mockResponse);
        verify(client).updateDeliveryPolicy(request);
    }
}