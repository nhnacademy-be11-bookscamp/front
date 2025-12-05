package store.bookscamp.front.payment.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    private MockMvc mvc;

    @InjectMocks
    private PaymentController paymentController;

    private final String baseUrl = "/payments";

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(paymentController)
                .build();
    }

    @Nested
    @DisplayName("GET /payments/success")
    class PaymentSuccessTest {

        @Test
        @DisplayName("결제 성공 페이지 요청 시 파라미터를 Model에 담아 success 뷰를 반환한다")
        void paymentSuccess_Success() throws Exception {
            // given
            String paymentKey = "test_payment_key_123";
            String orderId = "ORD-20231201-001";
            Integer amount = 50000;

            // when & then
            mvc.perform(get(baseUrl + "/success")
                            .param("paymentKey", paymentKey)
                            .param("orderId", orderId)
                            .param("amount", String.valueOf(amount)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("payment/success"))
                    .andExpect(model().attribute("paymentKey", paymentKey))
                    .andExpect(model().attribute("orderNumber", orderId))
                    .andExpect(model().attribute("amount", amount));
        }

        @Test
        @DisplayName("결제 성공 페이지 요청 시 금액이 0원이어도 정상 처리된다")
        void paymentSuccess_WithZeroAmount() throws Exception {
            // given
            String paymentKey = "test_payment_key_456";
            String orderId = "ORD-20231201-002";
            Integer amount = 0;

            // when & then
            mvc.perform(get(baseUrl + "/success")
                            .param("paymentKey", paymentKey)
                            .param("orderId", orderId)
                            .param("amount", String.valueOf(amount)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("payment/success"))
                    .andExpect(model().attribute("paymentKey", paymentKey))
                    .andExpect(model().attribute("orderNumber", orderId))
                    .andExpect(model().attribute("amount", 0));
        }
    }

    @Nested
    @DisplayName("GET /payments/fail")
    class PaymentFailTest {

        @Test
        @DisplayName("결제 실패 페이지 요청 시 에러 정보를 Model에 담아 fail 뷰를 반환한다")
        void paymentFail_Success() throws Exception {
            // given
            String errorCode = "PAY_PROCESS_CANCELED";
            String errorMessage = "사용자가 결제를 취소하였습니다.";

            // when & then
            mvc.perform(get(baseUrl + "/fail")
                            .param("code", errorCode)
                            .param("message", errorMessage))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("payment/fail"))
                    .andExpect(model().attribute("errorCode", errorCode))
                    .andExpect(model().attribute("errorMessage", errorMessage));
        }

        @Test
        @DisplayName("결제 실패 페이지 요청 시 다양한 에러 코드를 처리한다")
        void paymentFail_WithDifferentErrorCodes() throws Exception {
            // given
            String errorCode = "INVALID_CARD_COMPANY";
            String errorMessage = "유효하지 않은 카드사입니다.";

            // when & then
            mvc.perform(get(baseUrl + "/fail")
                            .param("code", errorCode)
                            .param("message", errorMessage))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("payment/fail"))
                    .andExpect(model().attribute("errorCode", errorCode))
                    .andExpect(model().attribute("errorMessage", errorMessage));
        }
    }
}