package store.bookscamp.front.auth.handler;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.AuthenticationException;
import store.bookscamp.front.common.exception.ConcurrentLoginException;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static org.assertj.core.api.Assertions.assertThat;

class CustomAuthenticationFailureHandlerTest {

    private CustomAuthenticationFailureHandler failureHandler;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        failureHandler = new CustomAuthenticationFailureHandler();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("ConcurrentLoginException 발생 시 concurrent 파라미터와 함께 리다이렉트된다")
    void onAuthenticationFailure_ConcurrentLogin() throws IOException, ServletException {
        ConcurrentLoginException exception = new ConcurrentLoginException("중복 로그인");

        failureHandler.onAuthenticationFailure(request, response, exception);

        assertThat(response.getRedirectedUrl()).contains("/login?concurrent");
    }

    @Test
    @DisplayName("DisabledException 발생 시 휴면 해제 페이지로 리다이렉트된다")
    void onAuthenticationFailure_Dormant() throws IOException, ServletException {
        DisabledException exception = new DisabledException("DORMANT_MEMBER");
        request.setParameter("username", "testuser");

        failureHandler.onAuthenticationFailure(request, response, exception);

        String encodedUsername = URLEncoder.encode("testuser", StandardCharsets.UTF_8);
        assertThat(response.getRedirectedUrl()).isEqualTo("/dormant?username=" + encodedUsername);
    }

    @Test
    @DisplayName("DisabledException이 중첩된 예외(Cause)로 있어도 휴면 해제 페이지로 이동한다")
    void onAuthenticationFailure_NestedDormant() throws IOException, ServletException {
        AuthenticationException exception = new InternalAuthenticationServiceException(
                "Wrapper",
                new DisabledException("DORMANT")
        );

        request.setParameter("username", "testuser");

        failureHandler.onAuthenticationFailure(request, response, exception);

        assertThat(response.getRedirectedUrl()).contains("/dormant");
    }

    @Test
    @DisplayName("일반적인 인증 실패 시 error 파라미터와 함께 리다이렉트된다")
    void onAuthenticationFailure_GeneralError() throws IOException, ServletException {
        BadCredentialsException exception = new BadCredentialsException("비번 틀림");

        failureHandler.onAuthenticationFailure(request, response, exception);

        assertThat(response.getRedirectedUrl()).contains("/login?error=true");
    }

    @Test
    @DisplayName("관리자 페이지에서 로그인 실패 시 관리자 로그인 페이지로 리다이렉트된다")
    void onAuthenticationFailure_AdminPage() throws IOException, ServletException {
        BadCredentialsException exception = new BadCredentialsException("Error");
        request.setRequestURI("/admin/login");

        failureHandler.onAuthenticationFailure(request, response, exception);

        assertThat(response.getRedirectedUrl()).contains("/admin/login?error=true");
    }
}