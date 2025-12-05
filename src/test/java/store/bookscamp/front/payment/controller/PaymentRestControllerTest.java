package store.bookscamp.front.payment.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import feign.FeignException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.bookscamp.front.payment.dto.PaymentCancelRequest;
import store.bookscamp.front.payment.dto.PaymentConfirmRequest;
import store.bookscamp.front.payment.dto.PaymentConfirmResponse;
import store.bookscamp.front.payment.feign.PaymentFeignClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentRestControllerTest {

    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());

    @Mock
    private PaymentFeignClient paymentFeignClient;

    @InjectMocks
    private PaymentRestController paymentRestController;

    private final String baseUrl = "/payments";

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(paymentRestController)
                .defaultResponseCharacterEncoding(StandardCharsets.UTF_8)
                .build();
    }

    @Nested
    @DisplayName("POST /payments/confirm")
    class ConfirmPaymentTest {

        @Test
        @DisplayName("결제 승인 요청 성공 시 200 OK와 PaymentConfirmResponse를 반환한다")
        void confirmPayment_Success() throws Exception {
            // given
            PaymentConfirmRequest request = new PaymentConfirmRequest(
                    "test_payment_key_123",
                    "ORD-20231201-001",
                    50000
            );

            PaymentConfirmResponse mockResponse = new PaymentConfirmResponse(
                    1L,
                    100L,
                    50000,
                    LocalDateTime.now()
            );

            given(paymentFeignClient.confirmPayment(any(PaymentConfirmRequest.class)))
                    .willReturn(ResponseEntity.ok(mockResponse));

            // when & then
            mvc.perform(post(baseUrl + "/confirm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.paymentId").value(1L))
                    .andExpect(jsonPath("$.orderId").value(100L))
                    .andExpect(jsonPath("$.paidAmount").value(50000));
        }

        @Test
        @DisplayName("0원 결제 승인 요청 시 정상적으로 처리된다")
        void confirmPayment_WithZeroAmount() throws Exception {
            // given
            PaymentConfirmRequest request = new PaymentConfirmRequest(
                    "zero_payment_key",
                    "ORD-20231201-002",
                    0
            );

            PaymentConfirmResponse mockResponse = new PaymentConfirmResponse(
                    2L,
                    101L,
                    0,
                    LocalDateTime.now()
            );

            given(paymentFeignClient.confirmPayment(any(PaymentConfirmRequest.class)))
                    .willReturn(ResponseEntity.ok(mockResponse));

            // when & then
            mvc.perform(post(baseUrl + "/confirm")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.paymentId").value(2L))
                    .andExpect(jsonPath("$.orderId").value(101L))
                    .andExpect(jsonPath("$.paidAmount").value(0));
        }
    }

    @Nested
    @DisplayName("POST /payments/cancel")
    class CancelPaymentTest {

        @Test
        @DisplayName("결제 취소 요청 성공 시 200 OK와 성공 메시지를 반환한다")
        void cancelPayment_Success() throws Exception {
            // given
            PaymentCancelRequest request = new PaymentCancelRequest(
                    100L,
                    "단순 변심"
            );

            given(paymentFeignClient.cancelPayment(any(PaymentCancelRequest.class)))
                    .willReturn(ResponseEntity.ok().build());

            // when & then
            mvc.perform(post(baseUrl + "/cancel")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("결제 취소 실패 시 에러 상태 코드와 에러 메시지를 반환한다")
        void cancelPayment_Failure() throws Exception {
            // given
            PaymentCancelRequest request = new PaymentCancelRequest(
                    100L,
                    "단순 변심"
            );

            FeignException feignException = mock(FeignException.class);
            given(feignException.status()).willReturn(400);
            given(feignException.contentUTF8()).willReturn("이미 취소된 결제입니다.");

            given(paymentFeignClient.cancelPayment(any(PaymentCancelRequest.class)))
                    .willThrow(feignException);

            // when & then
            mvc.perform(post(baseUrl + "/cancel")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("이미 취소된 결제입니다."));
        }

        @Test
        @DisplayName("결제 취소 시 다양한 취소 사유를 처리한다")
        void cancelPayment_WithDifferentReasons() throws Exception {
            // given
            PaymentCancelRequest request = new PaymentCancelRequest(
                    101L,
                    "상품 품절"
            );

            given(paymentFeignClient.cancelPayment(any(PaymentCancelRequest.class)))
                    .willReturn(ResponseEntity.ok().build());

            // when & then
            mvc.perform(post(baseUrl + "/cancel")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("결제 취소 시 서버 에러 발생 시 500 에러를 반환한다")
        void cancelPayment_ServerError() throws Exception {
            // given
            PaymentCancelRequest request = new PaymentCancelRequest(
                    102L,
                    "배송 지연"
            );

            FeignException feignException = mock(FeignException.class);
            given(feignException.status()).willReturn(500);
            given(feignException.contentUTF8()).willReturn("서버 내부 오류가 발생했습니다.");

            given(paymentFeignClient.cancelPayment(any(PaymentCancelRequest.class)))
                    .willThrow(feignException);

            // when & then
            mvc.perform(post(baseUrl + "/cancel")
                            .contentType(MediaType.APPLICATION_JSON)
                            .characterEncoding(StandardCharsets.UTF_8)
                            .accept(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andDo(print())
                    .andExpect(status().isInternalServerError())
                    .andExpect(content().string("서버 내부 오류가 발생했습니다."));
        }
    }
}