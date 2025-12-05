package store.bookscamp.front.common.config;

import feign.Request;
import feign.Response;
import feign.RetryableException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.bookscamp.front.auth.service.TokenRefreshService;
import store.bookscamp.front.common.exception.RefreshTokenExpiredException;

import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class FeignErrorDecoderTest {

    @Mock
    private TokenRefreshService tokenRefreshService;

    @InjectMocks
    private FeignErrorDecoder feignErrorDecoder;

    @Test
    @DisplayName("로그인/재발급 관련 경로는 401이어도 디코딩을 패스한다")
    void decode_LoginPath_ShouldSkip() {
        Request request = Request.create(Request.HttpMethod.POST, "/auth-server/login",
                Collections.emptyMap(), null, StandardCharsets.UTF_8, null);
        Response response = Response.builder()
                .request(request)
                .status(401)
                .reason("Unauthorized")
                .headers(Collections.emptyMap())
                .build();

        Exception exception = feignErrorDecoder.decode("methodKey", response);

        assertThat(exception)
                .isNotInstanceOf(RetryableException.class)
                .isNotInstanceOf(RefreshTokenExpiredException.class);
    }

    @Test
    @DisplayName("401 발생 시 토큰 갱신에 성공하면 RetryableException을 반환한다")
    void decode_RefreshSuccess_ShouldRetry() {
        Map<String, Collection<String>> headers = Map.of("Authorization", Collections.singletonList("Bearer old-token"));
        Request request = Request.create(Request.HttpMethod.GET, "/api/books",
                headers, null, StandardCharsets.UTF_8, null);

        Response response = Response.builder()
                .request(request)
                .status(401)
                .reason("Unauthorized")
                .headers(Collections.emptyMap())
                .build();

        given(tokenRefreshService.refreshTokens("Bearer old-token")).willReturn(true);

        Exception exception = feignErrorDecoder.decode("methodKey", response);

        assertThat(exception).isInstanceOf(RetryableException.class);
    }

    @Test
    @DisplayName("401 발생 시 토큰 갱신에 실패하면 RefreshTokenExpiredException을 던진다")
    void decode_RefreshFail_ShouldThrowException() {
        Request request = Request.create(Request.HttpMethod.GET, "/api/books",
                Collections.emptyMap(), null, StandardCharsets.UTF_8, null);

        Response response = Response.builder()
                .request(request)
                .status(401)
                .reason("Unauthorized")
                .headers(Collections.emptyMap())
                .build();

        given(tokenRefreshService.refreshTokens(null)).willReturn(false);

        assertThatThrownBy(() -> feignErrorDecoder.decode("methodKey", response))
                .isInstanceOf(RefreshTokenExpiredException.class);
    }

    @Test
    @DisplayName("401이 아닌 다른 에러 코드는 기본 디코더를 사용한다")
    void decode_OtherStatus_ShouldUseDefault() {
        Request request = Request.create(Request.HttpMethod.GET, "/api/books",
                Collections.emptyMap(), null, StandardCharsets.UTF_8, null);

        Response response = Response.builder()
                .request(request)
                .status(500)
                .reason("Server Error")
                .headers(Collections.emptyMap())
                .build();

        Exception exception = feignErrorDecoder.decode("methodKey", response);

        assertThat(exception)
                .isNotInstanceOf(RetryableException.class)
                .isNotInstanceOf(RefreshTokenExpiredException.class);
    }
}