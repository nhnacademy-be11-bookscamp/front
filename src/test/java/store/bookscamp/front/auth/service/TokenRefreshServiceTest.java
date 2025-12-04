package store.bookscamp.front.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import store.bookscamp.front.auth.dto.AccessTokenResponse;
import store.bookscamp.front.auth.repository.AuthFeignClient;
import store.bookscamp.front.auth.user.CustomMemberDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class TokenRefreshServiceTest {

    @Mock
    private AuthFeignClient authFeignClient;

    @InjectMocks
    private TokenRefreshService tokenRefreshService;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request, response));
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        RequestContextHolder.resetRequestAttributes();
        SecurityContextHolder.clearContext();
    }

    private void setContextToken(String token) {
        CustomMemberDetails details = new CustomMemberDetails("USER", token);
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(details, null, details.getAuthorities())
        );
    }

    @Test
    void refreshTokens_ShouldReturnTrue_WhenAlreadyRefreshedByAnotherThread() {
        setContextToken("Bearer new-token");

        boolean result = tokenRefreshService.refreshTokens("Bearer old-token");

        assertThat(result).isTrue();
    }

    @Test
    void refreshTokens_ShouldReturnFalse_WhenRequestAttributesNull() {
        setContextToken("Bearer token");
        RequestContextHolder.resetRequestAttributes();

        boolean result = tokenRefreshService.refreshTokens("Bearer token");

        assertThat(result).isFalse();
    }

    @Test
    void refreshTokens_ShouldReturnFalse_WhenRefreshTokenCookieMissing() {
        setContextToken("Bearer token");

        boolean result = tokenRefreshService.refreshTokens("Bearer token");

        assertThat(result).isFalse();
    }

    @Test
    void refreshTokens_ShouldReturnTrue_AndSetCookies_WhenRefreshSuccess_Member() {
        String oldToken = "Bearer old-token";
        setContextToken(oldToken);

        request.setCookies(new Cookie("refresh_token", "rt-value"));

        String newAccessToken = JWT.create().withClaim("role", "USER").sign(Algorithm.HMAC256("secret"));
        AccessTokenResponse tokenResponse = new AccessTokenResponse();
        tokenResponse.setAccessToken(newAccessToken);
        tokenResponse.setName("User");

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.SET_COOKIE, "refresh_token=new-rt-value; Path=/; HttpOnly");

        given(authFeignClient.reissue("rt-value"))
                .willReturn(ResponseEntity.ok().headers(headers).body(tokenResponse));

        boolean result = tokenRefreshService.refreshTokens(oldToken);

        assertThat(result).isTrue();

        assertThat(response.getHeaders(HttpHeaders.SET_COOKIE))
                .anyMatch(cookie -> cookie.contains("refresh_token=new-rt-value"));

        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    void refreshTokens_ShouldReturnTrue_AndSetCookies_WhenRefreshSuccess_Admin() {
        String oldToken = "Bearer old-token";
        setContextToken(oldToken);

        request.setCookies(new Cookie("refresh_token", "rt-value"));
        request.setSecure(true);
        request.addHeader("x-forwarded-proto", "https");

        String newAccessToken = JWT.create().withClaim("role", "ADMIN").sign(Algorithm.HMAC256("secret"));
        AccessTokenResponse tokenResponse = new AccessTokenResponse();
        tokenResponse.setAccessToken(newAccessToken);
        tokenResponse.setName("Admin");

        given(authFeignClient.reissue("rt-value"))
                .willReturn(ResponseEntity.ok().body(tokenResponse));

        boolean result = tokenRefreshService.refreshTokens(oldToken);

        assertThat(result).isTrue();
        assertThat(SecurityContextHolder.getContext().getAuthentication()).isNotNull();
    }

    @Test
    void refreshTokens_ShouldReturnFalse_WhenFeignExceptionOccurs() {
        String oldToken = "Bearer old-token";
        setContextToken(oldToken);

        request.setCookies(new Cookie("refresh_token", "rt-value"));

        given(authFeignClient.reissue(anyString())).willThrow(new RuntimeException("Feign Error"));

        boolean result = tokenRefreshService.refreshTokens(oldToken);

        assertThat(result).isFalse();
    }
}