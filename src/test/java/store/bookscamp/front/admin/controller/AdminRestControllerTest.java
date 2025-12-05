package store.bookscamp.front.admin.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.category.service.CategoryService;
import store.bookscamp.front.order.dto.OrderStatusUpdateRequest;
import store.bookscamp.front.order.dto.OrderStatusUpdateResponse;
import store.bookscamp.front.order.feign.OrderFeignClient;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private OrderFeignClient orderFeignClient;

    @MockitoBean private BookFeignClient bookFeignClient;
    @MockitoBean private CategoryService categoryService;
    @MockitoBean private RedisConnectionFactory redisConnectionFactory;

    @Test
    @DisplayName("주문 상태 변경을 요청한다 (POST /admin/orders/{orderId}/status)")
    void updateOrderStatus() throws Exception {
        OrderStatusUpdateResponse response = new OrderStatusUpdateResponse(
                1L,
                "ORDER-123",
                "SHIPPING",
                LocalDateTime.now()
        );

        given(orderFeignClient.updateOrderStatus(anyLong(), any(OrderStatusUpdateRequest.class)))
                .willReturn(ResponseEntity.ok(response));

        mockMvc.perform(post("/admin/orders/1/status")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"orderStatus\": \"SHIPPING\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStatus").value("SHIPPING"));
    }
}