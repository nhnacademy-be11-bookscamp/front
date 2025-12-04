package store.bookscamp.front.member.controller;

import feign.FeignException;
import feign.Request;
import feign.RequestTemplate;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import store.bookscamp.front.auth.repository.AuthFeignClient;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.category.service.CategoryService;
import store.bookscamp.front.member.controller.request.MemberStatusUpdateRequest;
import store.bookscamp.front.member.controller.request.MemberPasswordUpdateRequest;
import store.bookscamp.front.order.feign.OrderFeignClient;

import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberRestController.class)
@AutoConfigureMockMvc(addFilters = false)
class MemberRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberFeignClient memberFeignClient;

    @MockitoBean private AuthFeignClient authFeignClient;
    @MockitoBean private BookFeignClient bookFeignClient;
    @MockitoBean private CategoryService categoryService;
    @MockitoBean private RedisConnectionFactory redisConnectionFactory;
    @MockitoBean private OrderFeignClient orderFeignClient;

    @Test
    @DisplayName("ID 중복 확인 - 성공 (200 OK)")
    void checkId_Success() throws Exception {
        given(memberFeignClient.checkIdDuplicate(anyString())).willReturn(ResponseEntity.ok("OK"));

        mockMvc.perform(get("/members/check-id")
                        .param("id", "testuser"))
                .andExpect(status().isOk())
                .andExpect(content().string("OK"));
    }

    @Test
    @DisplayName("ID 중복 확인 - Feign 예외 발생 (상태 코드 전달)")
    void checkId_FeignException() throws Exception {
        Request request = Request.create(Request.HttpMethod.GET, "/members/check-id", new HashMap<>(), null, new RequestTemplate());
        FeignException exception = new FeignException.Conflict("Conflict", request, "Duplicated ID".getBytes(StandardCharsets.UTF_8), null);

        given(memberFeignClient.checkIdDuplicate(anyString())).willThrow(exception);

        mockMvc.perform(get("/members/check-id")
                        .param("id", "duplicateUser"))
                .andExpect(status().isConflict()) // 409
                .andExpect(content().string("Duplicated ID"));
    }

    @Test
    @DisplayName("ID 중복 확인 - 일반 서버 오류 발생 (500 Error)")
    void checkId_GeneralException() throws Exception {
        given(memberFeignClient.checkIdDuplicate(anyString())).willThrow(new RuntimeException("DB Connection Error"));

        mockMvc.perform(get("/members/check-id")
                        .param("id", "testuser"))
                .andExpect(status().isInternalServerError()) // 500
                .andExpect(content().string(containsString("서버 내부 오류가 발생했습니다")));
    }

    @Test
    @DisplayName("관리자: 회원 상태 변경")
    void updateMemberStatus() throws Exception {
        mockMvc.perform(post("/admin/members/{memberId}/status", 1L)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"status\": \"ACTIVE\"}"))
                .andExpect(status().isOk());

        verify(memberFeignClient).updateMemberStatus(any(MemberStatusUpdateRequest.class));
    }

    @Test
    @DisplayName("회원 탈퇴 - 인증된 상태 (로그아웃 처리 로직 실행)")
    void deleteMember_Authenticated() throws Exception {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken("user", "pw", Collections.emptyList())
        );

        mockMvc.perform(post("/members/delete")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(memberFeignClient).deleteMember();
    }

    @Test
    @DisplayName("회원 탈퇴 - 미인증 상태 (로그아웃 로직 건너뜀)")
    void deleteMember_Unauthenticated() throws Exception {
        SecurityContextHolder.clearContext();

        mockMvc.perform(post("/members/delete")
                        .with(csrf()))
                .andExpect(status().isOk());

        verify(memberFeignClient).deleteMember();
    }

    @Test
    @DisplayName("비밀번호 변경")
    void updatePassword() throws Exception {
        mockMvc.perform(put("/members/change-password")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"currentPassword\":\"oldPass123\", \"newPassword\":\"newPass123\"}"))
                .andExpect(status().isOk());

        verify(memberFeignClient).updatePassword(any(MemberPasswordUpdateRequest.class));
    }
}