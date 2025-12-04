package store.bookscamp.front.auth.handler;

import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class CustomOAuthSuccessHandlerTest {

    private CustomOAuthSuccessHandler successHandler;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        successHandler = new CustomOAuthSuccessHandler("/default-url");
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    @DisplayName("GUEST 권한을 가진 유저는 소셜 회원가입 페이지로 리다이렉트된다")
    void onAuthenticationSuccess_Guest() throws ServletException, IOException {
        Authentication authentication = mock(Authentication.class);
        given(authentication.getAuthorities()).willReturn((List) List.of(new SimpleGrantedAuthority("ROLE_GUEST")));

        successHandler.onAuthenticationSuccess(request, response, authentication);

        assertThat(response.getRedirectedUrl()).isEqualTo("/signup/social");
    }

    @Test
    @DisplayName("일반 유저는 토큰 쿠키를 설정하고 기본 URL로 리다이렉트된다")
    void onAuthenticationSuccess_User() throws ServletException, IOException {
        Authentication authentication = mock(Authentication.class);
        OAuth2User oauth2User = mock(OAuth2User.class);

        given(authentication.getAuthorities()).willReturn((List) List.of(new SimpleGrantedAuthority("ROLE_USER")));
        given(authentication.getPrincipal()).willReturn(oauth2User);

        Map<String, Object> attributes = Map.of(
                "rawAccessToken", "Bearer access-token",
                "rtCookieString", "refresh_token=rt; Path=/",
                "name", "홍길동"
        );
        given(oauth2User.getAttributes()).willReturn(attributes);

        successHandler.onAuthenticationSuccess(request, response, authentication);

        assertThat(response.getRedirectedUrl()).isEqualTo("/default-url");
        assertThat(response.getHeaders(HttpHeaders.SET_COOKIE)).hasSizeGreaterThan(0);
        assertThat(response.getHeaders(HttpHeaders.SET_COOKIE).toString()).contains("Authorization", "member_name");
    }

    @Test
    @DisplayName("OAuth2 속성에 필수 토큰 정보가 없으면 에러 로그를 남기고 리다이렉트만 수행한다")
    void onAuthenticationSuccess_MissingAttributes() throws ServletException, IOException {
        Authentication authentication = mock(Authentication.class);
        OAuth2User oauth2User = mock(OAuth2User.class);

        given(authentication.getAuthorities()).willReturn((List) List.of(new SimpleGrantedAuthority("ROLE_USER")));
        given(authentication.getPrincipal()).willReturn(oauth2User);

        given(oauth2User.getAttributes()).willReturn(Map.of("name", "홍길동"));

        successHandler.onAuthenticationSuccess(request, response, authentication);

        assertThat(response.getRedirectedUrl()).isEqualTo("/default-url");
        assertThat(response.getHeaders(HttpHeaders.SET_COOKIE)).isEmpty();
    }
}