package store.bookscamp.front.order.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import store.bookscamp.front.address.controller.response.AddressListResponse;
import store.bookscamp.front.address.feign.AddressFeignClient;
import store.bookscamp.front.member.controller.MemberFeignClient;
import store.bookscamp.front.member.controller.response.MemberGetResponse;
import store.bookscamp.front.order.dto.*;
import store.bookscamp.front.order.feign.OrderFeignClient;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class OrderControllerTest {

    private MockMvc mvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private OrderFeignClient orderFeignClient;

    @Mock
    private AddressFeignClient addressFeignClient;

    @Mock
    private MemberFeignClient memberFeignClient;

    @InjectMocks
    private OrderController orderController;

    private final String baseUrl = "/orders";
    private final Cookie authCookie = new Cookie("Authorization", "valid_token");

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(orderController)
                .build();
    }


    private OrderDetailResponse createOrderDetailResponse() {
        return new OrderDetailResponse(
                100L,
                LocalDateTime.now(),
                "ORDER_COMPLETED",
                List.of(new OrderDetailResponse.OrderDetailItemResponse(1L, "Book A", 2, 10000, 20000)),
                "수령인",
                "010-1234-5678",
                "[12345] 서울시 강남구",
                "문 앞",
                20000,
                3000,
                0,
                1000,
                0,
                22000
        );
    }

    @Nested
    @DisplayName("POST /orders/prepare")
    class PrepareOrderTest {

        @Test
        @DisplayName("회원 주문서 준비 요청 성공 시 주소록과 회원 정보를 Model에 담아 반환한다")
        void prepareOrder_Member_Success() throws Exception {
            // given
            OrderPrepareRequest prepareRequest = new OrderPrepareRequest(List.of(), "CART");
            OrderPrepareResponse mockPrepareResponse = new OrderPrepareResponse(List.of(), null, 0, List.of(), List.of());
            MemberGetResponse mockMemberInfo = new MemberGetResponse(
                    "user123",
                    "사용자 이름",
                    "test@example.com",
                    "010-1111-2222",
                    1000,
                    java.time.LocalDate.now()
            );
            AddressListResponse.AddressResponse defaultAddress = new AddressListResponse.AddressResponse(1L, "도로명1", "상세1", 12345, true, "101동 101호");
            AddressListResponse.AddressResponse nonDefaultAddress = new AddressListResponse.AddressResponse(2L, "도로명2", "상세2", 54321, false, "202호");
            AddressListResponse mockAddressList = new AddressListResponse(List.of(nonDefaultAddress, defaultAddress));

            given(orderFeignClient.prepareOrder(any(OrderPrepareRequest.class)))
                    .willReturn(ResponseEntity.ok(mockPrepareResponse));
            given(memberFeignClient.getMember())
                    .willReturn(mockMemberInfo);
            given(addressFeignClient.getAddresses())
                    .willReturn(ResponseEntity.ok(mockAddressList));

            mvc.perform(post(baseUrl + "/prepare")
                            .cookie(authCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(prepareRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("order/order-prepare"))
                    .andExpect(model().attribute("isMember", true))
                    .andExpect(model().attribute("username", "user123"));
        }

        @Test
        @DisplayName("비회원 주문서 준비 요청 성공 시 주소록과 회원 정보를 null로 담아 반환한다")
        void prepareOrder_NonMember_Success() throws Exception {
            // given
            OrderPrepareRequest prepareRequest = new OrderPrepareRequest(List.of(), "DIRECT");
            OrderPrepareResponse mockPrepareResponse = new OrderPrepareResponse(List.of(), null, 0, List.of(), List.of());

            given(orderFeignClient.prepareOrder(any(OrderPrepareRequest.class)))
                    .willReturn(ResponseEntity.ok(mockPrepareResponse));

            // when & then
            mvc.perform(post(baseUrl + "/prepare")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(prepareRequest)))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(view().name("order/order-prepare"))
                    .andExpect(model().attribute("isMember", false));
        }
    }

    @Nested
    @DisplayName("POST /orders")
    class CreateOrderTest {

        @Test
        @DisplayName("회원 주문 생성 요청 성공 시 200 OK와 응답 데이터를 반환한다")
        void createOrder_Member_Success() throws Exception {
            OrderCreateRequest request = new OrderCreateRequest(null, null, null, 0, null, "CART");
            OrderCreateResponse mockResponse = new OrderCreateResponse(10L, "ORD-1", 50000);

            given(orderFeignClient.createOrder(any(OrderCreateRequest.class)))
                    .willReturn(ResponseEntity.ok(mockResponse));

            mvc.perform(post(baseUrl)
                            .cookie(authCookie)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(content().json(objectMapper.writeValueAsString(mockResponse)));
        }

    }

    @Nested
    @DisplayName("GET /orders/list")
    class GetOrderListTest {

        @Test
        @DisplayName("주문 목록 조회 성공 시 페이징 정보와 리스트를 Model에 담아 반환한다")
        void getOrderList_Success() throws Exception {
            // given
            OrderListResponse order1 = new OrderListResponse(1L, LocalDateTime.now(), "Book A 외 1건", 2, 50000);
            PageResponse<OrderListResponse> mockPageResponse = new PageResponse<>(
                    List.of(order1), 1, 5, 1, 1, true, true
            );

            given(orderFeignClient.getOrderList(eq(0), eq(5)))
                    .willReturn(ResponseEntity.ok(mockPageResponse));

            // when & then
            mvc.perform(get(baseUrl + "/list")
                            .param("page", "0")
                            .param("size", "5"))
                    .andExpect(status().isOk())
                    .andExpect(view().name("order/order-list"));
        }
    }

    @Nested
    @DisplayName("GET /orders/{orderId}")
    class GetOrderDetailTest {

        @Test
        @DisplayName("주문 상세 조회 성공 시 OrderDetailResponse를 Model에 담아 반환한다")
        void getOrderDetail_Success() throws Exception {
            // given
            OrderDetailResponse mockResponse = createOrderDetailResponse();

            given(orderFeignClient.getOrderDetail(eq(100L)))
                    .willReturn(ResponseEntity.ok(mockResponse));

            // when & then
            mvc.perform(get(baseUrl + "/{orderId}", 100L))
                    .andExpect(status().isOk())
                    .andExpect(view().name("order/order-detail"));
        }
    }

    @Nested
    @DisplayName("POST /orders/non-member/detail")
    class GetNonMemberDetailTest {

        private final String nonMemberOrderNumber = "NM-12345"; // password
        private final String password = "valid_password";

        @Test
        @DisplayName("비회원 주문 상세 조회 성공 시 응답 데이터와 isMember=false를 Model에 담아 반환한다")
        void getNonMemberDetail_Success() throws Exception {
            // given
            OrderDetailResponse mockResponse = createOrderDetailResponse();

            given(orderFeignClient.getNonMemberOrderDetail(
                    eq(nonMemberOrderNumber),
                    any(NonMemberOrderRequest.class)))
                    .willReturn(ResponseEntity.ok(mockResponse));

            mvc.perform(post(baseUrl + "/non-member/detail")
                            .param("orderNumber", nonMemberOrderNumber)
                            .param("password", password))
                    .andExpect(status().isOk())
                    .andExpect(view().name("order/non-member-detail"))
                    .andExpect(model().attribute("isMember", false));
        }
    }
}
