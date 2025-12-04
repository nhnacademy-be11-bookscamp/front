package store.bookscamp.front.admin.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.category.service.CategoryService;
import store.bookscamp.front.order.dto.OrderDetailResponse;
import store.bookscamp.front.order.dto.OrderListResponse;
import store.bookscamp.front.order.dto.PageResponse;
import store.bookscamp.front.order.feign.OrderFeignClient;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AdminController.class)
@AutoConfigureMockMvc(addFilters = false)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean private OrderFeignClient orderFeignClient;
    @MockitoBean private BookFeignClient bookFeignClient;
    @MockitoBean private CategoryService categoryService;
    @MockitoBean private RedisConnectionFactory redisConnectionFactory;

    @Test
    @DisplayName("로그인 페이지 호출 시 이미 인증된 사용자는 리다이렉트된다")
    @WithMockUser
    void loginPage_Authenticated() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "admin", "password", Collections.singletonList(new SimpleGrantedAuthority("ROLE_ADMIN")));

        mockMvc.perform(get("/admin/login").principal(auth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
    }

    @Test
    @DisplayName("로그인 페이지 호출 시 미인증 사용자는 로그인 뷰를 반환한다")
    void loginPage_Unauthenticated() throws Exception {
        mockMvc.perform(get("/admin/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/login"));
    }

    @Test
    @DisplayName("관리자 대시보드 및 인덱스 페이지 조회")
    void dashboardAndIndex() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/index"));

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/dashboard"));
    }

    @Test
    @DisplayName("관리자 주문 목록 페이지를 조회한다 (데이터가 있을 때)")
    void getAdminOrderList() throws Exception {
        PageResponse<OrderListResponse> pageResponse = new PageResponse<>(
                List.of(), 1, 10, 1, 0L, true, true
        );
        given(orderFeignClient.getAdminOrderList(anyInt(), anyInt(), anyString()))
                .willReturn(ResponseEntity.ok(pageResponse));

        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/order-list"))
                .andExpect(model().attributeExists("orderPage", "orders"));
    }

    @Test
    @DisplayName("관리자 주문 목록 페이지 조회 시 응답 본문이 null이면 빈 리스트를 반환한다")
    void getAdminOrderList_NullBody() throws Exception {
        given(orderFeignClient.getAdminOrderList(anyInt(), anyInt(), anyString()))
                .willReturn(ResponseEntity.ok(null));

        mockMvc.perform(get("/admin/orders"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/order-list"))
                .andExpect(model().attribute("orders", List.of()));
    }

    @Test
    @DisplayName("관리자 주문 상세 페이지를 조회한다")
    void getAdminOrderDetail() throws Exception {
        OrderDetailResponse mockResponse = mock(OrderDetailResponse.class);
        given(orderFeignClient.getAdminOrderDetail(anyLong()))
                .willReturn(ResponseEntity.ok(mockResponse));

        mockMvc.perform(get("/admin/orders/{orderId}", 1L))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/order-detail"))
                .andExpect(model().attributeExists("order"))
                .andExpect(model().attribute("isAdmin", true));
    }
}