package store.bookscamp.front.auth.handler;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import store.bookscamp.front.auth.dto.LoginAuthDetails;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class CustomAuthenticationSuccessHandlerTest {

    private CustomAuthenticationSuccessHandler successHandler;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        successHandler = new CustomAuthenticationSuccessHandler("/default");
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("로그인 성공 시 토큰과 사용자 이름 쿠키를 설정하고 리다이렉트한다")
    void onAuthenticationSuccess() throws IOException, ServletException {
        Authentication authentication = mock(Authentication.class);
        LoginAuthDetails details = mock(LoginAuthDetails.class);

        given(authentication.getDetails()).willReturn(details);
        given(details.getRawAccessToken()).willReturn("Bearer jwt-token");
        given(details.getRtCookieString()).willReturn("refresh_token=rt; Path=/");
        given(details.getName()).willReturn("홍길동");

        // X-Forwarded-Proto 헤더 설정 (Secure 쿠키 테스트)
        request.addHeader("x-forwarded-proto", "https");

        successHandler.onAuthenticationSuccess(request, response, authentication);

        assertThat(response.getRedirectedUrl()).isEqualTo("/default");

        // Authorization 쿠키 확인
        assertThat(response.getHeaders(HttpHeaders.SET_COOKIE).toString())
                .contains("Authorization=jwt-token")
                .contains("Secure")
                .contains("HttpOnly");

        // Refresh Token 쿠키 확인
        assertThat(response.getHeaders(HttpHeaders.SET_COOKIE).toString())
                .contains("refresh_token=rt");

        // Member Name 쿠키 확인 (URL Encoded)
        assertThat(response.getHeaders(HttpHeaders.SET_COOKIE).toString())
                .contains("member_name=%ED%99%8D%EA%B8%B8%EB%8F%99");
    }

    @Test
    @DisplayName("토큰 정보가 없으면 쿠키를 설정하지 않고 리다이렉트만 한다")
    void onAuthenticationSuccess_NoToken() throws IOException, ServletException {
        Authentication authentication = mock(Authentication.class);
        LoginAuthDetails details = mock(LoginAuthDetails.class);

        given(authentication.getDetails()).willReturn(details);
        given(details.getRawAccessToken()).willReturn(null);
        given(details.getRtCookieString()).willReturn(null);
        given(details.getName()).willReturn(null);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        assertThat(response.getRedirectedUrl()).isEqualTo("/default");
        assertThat(response.getHeaders(HttpHeaders.SET_COOKIE)).isEmpty();
    }
}