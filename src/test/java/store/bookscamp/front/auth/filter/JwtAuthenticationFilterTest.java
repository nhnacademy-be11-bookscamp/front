package store.bookscamp.front.auth.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;
import store.bookscamp.front.auth.user.CustomAdminDetails;
import store.bookscamp.front.auth.user.CustomMemberDetails;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtAuthenticationFilter jwtAuthenticationFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private FilterChain filterChain;

    @BeforeEach
    void setUp() {
        jwtAuthenticationFilter = new JwtAuthenticationFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = mock(FilterChain.class);

        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("쿠키가 아예 없으면 필터를 그냥 통과한다")
    void doFilterInternal_NoCookies() throws ServletException, IOException {
        request.setCookies((Cookie[]) null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("Authorization 쿠키가 없으면 필터를 그냥 통과한다")
    void doFilterInternal_NoAuthCookie() throws ServletException, IOException {
        request.setCookies(new Cookie("JSESSIONID", "1234"));

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("ADMIN 권한 토큰이 있으면 CustomAdminDetails로 인증된다")
    void doFilterInternal_ValidToken_Admin() throws ServletException, IOException {
        String token = createTestToken("ADMIN");
        Cookie authCookie = new Cookie("Authorization", token);
        request.setCookies(authCookie);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isInstanceOf(CustomAdminDetails.class);
    }

    @Test
    @DisplayName("일반(MEMBER) 권한 토큰이 있으면 CustomMemberDetails로 인증된다")
    void doFilterInternal_ValidToken_Member() throws ServletException, IOException {
        String token = createTestToken("USER"); // ADMIN이 아닌 값
        Cookie authCookie = new Cookie("Authorization", token);
        request.setCookies(authCookie);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
                .isInstanceOf(CustomMemberDetails.class);
    }

    @Test
    @DisplayName("토큰 형식이 잘못되었으면(JWTDecodeException) 로그를 남기고 인증 없이 통과한다")
    void doFilterInternal_InvalidToken_DecodeException() throws ServletException, IOException {
        String invalidToken = "invalid.token.value";
        Cookie authCookie = new Cookie("Authorization", invalidToken);
        request.setCookies(authCookie);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
    }

    @Test
    @DisplayName("예기치 않은 오류(Exception)가 발생하면 로그를 남기고 인증 없이 통과한다")
    void doFilterInternal_GeneralException() throws ServletException, IOException {
        String token = "some.token.value";
        Cookie authCookie = new Cookie("Authorization", token);
        request.setCookies(authCookie);

        try (MockedStatic<JWT> mockedJwt = mockStatic(JWT.class)) {
            mockedJwt.when(() -> JWT.decode(anyString()))
                    .thenThrow(new RuntimeException("Unexpected Error"));

            jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

            verify(filterChain).doFilter(request, response);
            assertThat(SecurityContextHolder.getContext().getAuthentication()).isNull();
        }
    }

    private String createTestToken(String role) {
        return JWT.create()
                .withClaim("role", role)
                .sign(Algorithm.HMAC256("secret"));
    }
}