package store.bookscamp.front.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import store.bookscamp.front.auth.repository.AuthFeignClient;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.category.service.CategoryService;
import store.bookscamp.front.order.feign.OrderFeignClient;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.doThrow;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(DormantMemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class DormantMemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthFeignClient authFeignClient;

    @MockitoBean private MemberFeignClient memberFeignClient;
    @MockitoBean private BookFeignClient bookFeignClient;
    @MockitoBean private CategoryService categoryService;
    @MockitoBean private RedisConnectionFactory redisConnectionFactory;
    @MockitoBean private OrderFeignClient orderFeignClient;

    @Test
    @DisplayName("휴면 해제 페이지 조회")
    void dormantPage() throws Exception {
        mockMvc.perform(get("/dormant"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/dormant"));
    }

    @Test
    @DisplayName("인증번호 발송 성공")
    void sendDormantCode_Success() throws Exception {
        mockMvc.perform(post("/dormant/send")
                        .with(csrf())
                        .param("username", "testuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/dormant?**"))
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("sent", "true"));
    }

    @Test
    @DisplayName("인증번호 발송 실패")
    void sendDormantCode_Fail() throws Exception {
        doThrow(new RuntimeException("Error")).when(authFeignClient).sendDormantCode(anyMap());

        mockMvc.perform(post("/dormant/send")
                        .with(csrf())
                        .param("username", "testuser"))
                .andExpect(status().is3xxRedirection())
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("error", "send_failed"));
    }

    @Test
    @DisplayName("인증번호 검증 성공")
    void verifyDormantCode_Success() throws Exception {
        mockMvc.perform(post("/dormant/verify")
                        .with(csrf())
                        .param("username", "testuser")
                        .param("code", "123456"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/login?**"))
                // [수정] 리다이렉트 시 boolean 값은 문자열 "true"로 변환되어 전달됨
                .andExpect(model().attribute("logout", "true"));
    }

    @Test
    @DisplayName("인증번호 검증 실패")
    void verifyDormantCode_Fail() throws Exception {
        doThrow(new RuntimeException("Invalid Code")).when(authFeignClient).verifyDormantCode(anyMap());

        mockMvc.perform(post("/dormant/verify")
                        .with(csrf())
                        .param("username", "testuser")
                        .param("code", "wrong"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrlPattern("/dormant?**"))
                .andExpect(model().attribute("username", "testuser"))
                .andExpect(model().attribute("sent", "true"))
                .andExpect(model().attribute("error", "invalid"));
    }
}