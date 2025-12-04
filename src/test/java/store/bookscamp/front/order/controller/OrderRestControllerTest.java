package store.bookscamp.front.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import jakarta.servlet.http.Cookie;
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
import store.bookscamp.front.order.dto.OrderCreateRequest;
import store.bookscamp.front.order.dto.OrderCreateResponse;
import store.bookscamp.front.order.dto.OrderReturnRequest;
import store.bookscamp.front.order.dto.OrderReturnResponse;
import store.bookscamp.front.order.feign.OrderFeignClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class OrderRestControllerTest {

    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private OrderFeignClient orderFeignClient;

    @InjectMocks
    private OrderRestController orderRestController;

    private final String baseUrl = "/orders";
    private final Cookie authCookie = new Cookie("Authorization", "valid_token");

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(orderRestController)
                .build();
    }

    @Nested
    @DisplayName("POST /orders")
    class CreateOrderTest {

        @Test
        @DisplayName("주문 생성 요청 성공 시 200 OK와 응답 데이터를 반환한다")
        void createOrder_Success() throws Exception {
            // given
            OrderCreateRequest request = new OrderCreateRequest(null, null, null, 0, null, "CART");
            OrderCreateResponse mockResponse = new OrderCreateResponse(10L, "ORD-1", 50000);

            given(orderFeignClient.createOrder(any(OrderCreateRequest.class)))
                    .willReturn(ResponseEntity.ok(mockResponse));

            // when & then
            mvc.perform(post(baseUrl)
                            .cookie(authCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
        }
    }

    @Nested
    @DisplayName("POST /orders/{orderId}/return")
    class ReturnOrderTest {

        @Test
        @DisplayName("반품 신청 성공 시 200 OK와 응답 데이터를 반환한다")
        void returnOrder_Success() throws Exception {
            // given
            Long orderId = 100L;
            OrderReturnRequest request = new OrderReturnRequest("CHANGE_OF_MIND");
            OrderReturnResponse mockResponse = new OrderReturnResponse("ORD-1", 5000L);

            given(orderFeignClient.returnOrder(eq(orderId), any(OrderReturnRequest.class)))
                    .willReturn(ResponseEntity.ok(mockResponse));

            // when & then
            mvc.perform(post(baseUrl + "/{orderId}/return", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
        }

        @Test
        @DisplayName("반품 신청 실패 시 에러 상태 코드와 에러 메시지를 반환한다")
        void returnOrder_Failure() throws Exception {
            // given
            Long orderId = 100L;
            OrderReturnRequest request = new OrderReturnRequest("CHANGE_OF_MIND");

            FeignException feignException = mock(FeignException.class);
            given(feignException.status()).willReturn(400);
            given(feignException.contentUTF8()).willReturn("반품 기한이 초과되었습니다.");

            given(orderFeignClient.returnOrder(eq(orderId), any(OrderReturnRequest.class)))
                    .willThrow(feignException);

            // when & then
            mvc.perform(post(baseUrl + "/{orderId}/return", orderId)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isBadRequest())
                    .andExpect(content().string("반품 기한이 초과되었습니다."));
        }
    }
}