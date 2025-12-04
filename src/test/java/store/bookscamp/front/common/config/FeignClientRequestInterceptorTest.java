package store.bookscamp.front.common.config;

import feign.RequestTemplate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import store.bookscamp.front.auth.user.TokenDetails;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class FeignClientRequestInterceptorTest {

    private FeignClientRequestInterceptor interceptor;

    @BeforeEach
    void setUp() {
        interceptor = new FeignClientRequestInterceptor();
        SecurityContextHolder.clearContext();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("로그인 관련 경로는 헤더를 추가하지 않는다")
    void apply_LoginPath_NoHeader() {
        RequestTemplate template = new RequestTemplate();
        template.target("http://localhost");
        template.uri("/auth-server/login");

        interceptor.apply(template);

        assertThat(template.headers()).doesNotContainKey("Authorization");
    }

    @Test
    @DisplayName("인증 정보가 없으면 헤더를 추가하지 않는다")
    void apply_NoAuth_NoHeader() {
        RequestTemplate template = new RequestTemplate();
        template.target("http://localhost");
        template.uri("/api/books");

        interceptor.apply(template);

        assertThat(template.headers()).doesNotContainKey("Authorization");
    }

    @Test
    @DisplayName("인증된 사용자라면 토큰을 헤더에 추가한다")
    void apply_Authenticated_AddHeader() {
        RequestTemplate template = new RequestTemplate();
        template.target("http://localhost");
        template.uri("/api/orders");

        TokenDetails principal = mock(TokenDetails.class);
        given(principal.getRawJwtToken()).willReturn("Bearer token");

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        interceptor.apply(template);

        assertThat(template.headers()).containsKey("Authorization");
        assertThat(template.headers().get("Authorization")).contains("Bearer token");
    }

    @Test
    @DisplayName("토큰 값이 비어있으면 헤더를 추가하지 않는다")
    void apply_EmptyToken_NoHeader() {
        RequestTemplate template = new RequestTemplate();
        template.target("http://localhost");
        template.uri("/api/orders");

        TokenDetails principal = mock(TokenDetails.class);
        given(principal.getRawJwtToken()).willReturn(""); // Empty Token

        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(principal, null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);

        interceptor.apply(template);

        assertThat(template.headers()).doesNotContainKey("Authorization");
    }
}