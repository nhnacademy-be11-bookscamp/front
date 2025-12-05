package store.bookscamp.front.member.controller;

import feign.FeignException;
import feign.Request;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import store.bookscamp.front.auth.repository.AuthFeignClient;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.category.service.CategoryService;
import store.bookscamp.front.common.pagination.RestPageImpl;
import store.bookscamp.front.member.controller.request.MemberCreateRequest;
import store.bookscamp.front.member.controller.request.MemberUpdateRequest;
import store.bookscamp.front.member.controller.response.MemberGetResponse;
import store.bookscamp.front.member.controller.response.MemberPageResponse;
import store.bookscamp.front.rank.controller.request.RankGetRequest;
import store.bookscamp.front.rank.feign.RankFeignClient;

import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@AutoConfigureMockMvc(addFilters = false)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MemberFeignClient memberFeignClient;

    @MockitoBean
    private RankFeignClient rankFeignClient;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private BookFeignClient bookFeignClient;
    @MockitoBean
    private CategoryService categoryService;
    @MockitoBean
    private RedisConnectionFactory redisConnectionFactory;
    @MockitoBean
    private AuthFeignClient authFeignClient;

    @Test
    @DisplayName("로그인 페이지 - 미인증 사용자")
    void loginPage_Unauthenticated() throws Exception {
        mockMvc.perform(get("/login"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/login"));
    }

    @Test
    @DisplayName("로그인 페이지 - 인증된 사용자 리다이렉트")
    void loginPage_Authenticated() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "user", "pw", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(get("/login").principal(auth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("회원가입 페이지 - 미인증 사용자")
    void showPage_Unauthenticated() throws Exception {
        mockMvc.perform(get("/signup"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/signup"));
    }

    @Test
    @DisplayName("회원가입 페이지 - 인증된 사용자 리다이렉트")
    void showPage_Authenticated() throws Exception {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                "user", "pw", Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));

        mockMvc.perform(get("/signup").principal(auth))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }

    @Test
    @DisplayName("소셜 회원가입 폼 페이지")
    void showSocialSignupForm() throws Exception {
        mockMvc.perform(get("/signup/social"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/signup-social"));
    }

    @Test
    @DisplayName("마이페이지 조회")
    void getMember() throws Exception {
        MemberGetResponse memberResponse = new MemberGetResponse("user", "홍길동", "test@test.com", "010-1234-5678", 1000, LocalDate.now());

        RankGetRequest rankResponse = new RankGetRequest("Gold", 100);

        given(memberFeignClient.getMember()).willReturn(memberResponse);
        given(rankFeignClient.getRank()).willReturn(ResponseEntity.ok(rankResponse));

        mockMvc.perform(get("/mypage"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/mypage"))
                .andExpect(model().attributeExists("memberInfo", "rank"));
    }

    @Test
    @DisplayName("회원 정보 수정 페이지")
    void editInfo() throws Exception {
        MemberGetResponse memberResponse = new MemberGetResponse("user", "홍길동", "test@test.com", "010-1234-5678", 1000, LocalDate.now());
        given(memberFeignClient.getMember()).willReturn(memberResponse);

        mockMvc.perform(get("/mypage/edit-info"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/edit-info"))
                .andExpect(model().attributeExists("memberInfo"));
    }

    @Test
    @DisplayName("비밀번호 변경 페이지")
    void changePassword() throws Exception {
        MemberGetResponse memberResponse = new MemberGetResponse("user", "홍길동", "test@test.com", "010-1234-5678", 1000, LocalDate.now());
        given(memberFeignClient.getMember()).willReturn(memberResponse);

        mockMvc.perform(get("/mypage/change-password"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/change-password"))
                .andExpect(model().attributeExists("memberInfo"));
    }

    @Test
    @DisplayName("일반 회원가입 성공")
    void createMember_Success() throws Exception {
        given(passwordEncoder.encode(any())).willReturn("encodedPw");
        given(memberFeignClient.createMember(any(MemberCreateRequest.class))).willReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/members")
                        .with(csrf())
                        .param("username", "testuser")
                        .param("password", "password123")
                        .param("name", "Test")
                        .param("email", "test@test.com")
                        .param("phone", "010-1234-5678")
                        .param("birthDate", "2000-01-01"))
                .andExpect(status().isOk())
                .andExpect(view().name("member/login"));
    }

    @Test
    @DisplayName("일반 회원가입 실패 - Feign 예외")
    void createMember_Fail() throws Exception {
        Request request = Request.create(Request.HttpMethod.POST, "/members", new HashMap<>(), null, null, null);
        FeignException exception = new FeignException.BadRequest("Bad Request", request, "Error Details".getBytes(StandardCharsets.UTF_8), null);

        given(passwordEncoder.encode(any())).willReturn("encodedPw");
        doThrow(exception).when(memberFeignClient).createMember(any(MemberCreateRequest.class));

        mockMvc.perform(post("/members")
                        .with(csrf())
                        .param("username", "testuser")
                        .param("password", "password123")
                        .param("name", "Test")
                        .param("email", "test@test.com")
                        .param("phone", "010-1234-5678")
                        .param("birthDate", "2000-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup"))
                .andExpect(flash().attributeExists("errorMessage"));
    }

    @Test
    @DisplayName("소셜 회원가입 성공")
    void createSocialMember_Success() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("oauth_username", "social_user");

        given(memberFeignClient.createMember(any(MemberCreateRequest.class))).willReturn(ResponseEntity.ok().build());

        mockMvc.perform(post("/members/social")
                        .with(csrf())
                        .session(session)
                        .param("name", "Social")
                        .param("email", "s@test.com")
                        .param("phone", "010-1111-2222")
                        .param("birthDate", "2000-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }

    @Test
    @DisplayName("소셜 회원가입 실패 - 409 Conflict")
    void createSocialMember_Fail_409() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("oauth_username", "social_user");

        FeignException exception = mock(FeignException.class);
        given(exception.status()).willReturn(409);
        doThrow(exception).when(memberFeignClient).createMember(any(MemberCreateRequest.class));

        mockMvc.perform(post("/members/social")
                        .with(csrf())
                        .session(session)
                        .param("name", "Social")
                        .param("email", "s@test.com")
                        .param("phone", "010-1111-2222")
                        .param("birthDate", "2000-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup/social"))
                .andExpect(flash().attribute("errorMessage", "이미 가입된 이메일 또는 전화번호입니다."));
    }

    @Test
    @DisplayName("소셜 회원가입 실패 - 기타 에러")
    void createSocialMember_Fail_Other() throws Exception {
        MockHttpSession session = new MockHttpSession();
        session.setAttribute("oauth_username", "social_user");

        FeignException exception = mock(FeignException.class);
        given(exception.status()).willReturn(500);
        doThrow(exception).when(memberFeignClient).createMember(any(MemberCreateRequest.class));

        mockMvc.perform(post("/members/social")
                        .with(csrf())
                        .session(session)
                        .param("name", "Social")
                        .param("email", "s@test.com")
                        .param("phone", "010-1111-2222")
                        .param("birthDate", "2000-01-01"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/signup/social"))
                .andExpect(flash().attribute("errorMessage", "회원가입 중 오류 발생"));
    }

    @Test
    @DisplayName("회원 정보 수정 성공")
    void updateMemberByForm_Success() throws Exception {
        mockMvc.perform(put("/members/update-info")
                        .with(csrf())
                        .param("name", "New Name")
                        .param("email", "new@test.com")
                        .param("phone", "010-9999-8888"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/mypage"));
    }

    @Test
    @DisplayName("회원 정보 수정 실패")
    void updateMemberByForm_Fail() throws Exception {
        doThrow(FeignException.class).when(memberFeignClient).updateMember(any(MemberUpdateRequest.class));

        mockMvc.perform(put("/members/update-info")
                        .with(csrf())
                        .param("name", "New Name")
                        .param("email", "new@test.com")
                        .param("phone", "010-9999-8888"))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/mypage/edit-info"));
    }

    @Test
    @DisplayName("관리자 회원 목록 조회")
    void getMemberList() throws Exception {
        RestPageImpl<MemberPageResponse> page = new RestPageImpl<>(
                List.of(),
                0,
                10,
                0L,
                1,
                true,
                true,
                0,
                true
        );
        given(memberFeignClient.getAllMembers(any(Pageable.class))).willReturn(ResponseEntity.ok(page));

        mockMvc.perform(get("/admin/members"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/member-list"))
                .andExpect(model().attributeExists("members"));
    }
}