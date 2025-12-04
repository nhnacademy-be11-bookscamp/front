package store.bookscamp.front.auth.provider;

import com.auth0.jwt.exceptions.JWTDecodeException;
import feign.FeignException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import store.bookscamp.front.admin.controller.request.AdminLoginRequest;
import store.bookscamp.front.auth.dto.AccessTokenResponse;
import store.bookscamp.front.auth.repository.AuthFeignClient;
import store.bookscamp.front.auth.user.CustomAdminDetails;
import store.bookscamp.front.common.exception.ConcurrentLoginException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class AdminAuthenticationProviderTest {

    @Mock
    private AuthFeignClient authFeignClient;

    @InjectMocks
    private AdminAuthenticationProvider adminAuthenticationProvider;

    @Test
    @DisplayName("관리자 로그인 성공 시 AdminDetails가 담긴 Authentication을 반환한다")
    void authenticate_Success() {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "pw");
        AccessTokenResponse tokenResponse = new AccessTokenResponse();
        tokenResponse.setAccessToken("admin-token");
        tokenResponse.setName("Admin");
        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "refresh_token=rt");

        given(authFeignClient.doLogin(any(AdminLoginRequest.class)))
                .willReturn(ResponseEntity.ok().headers(headers).body(tokenResponse));

        Authentication result = adminAuthenticationProvider.authenticate(auth);

        assertThat(result.isAuthenticated()).isTrue();
        assertThat(result.getPrincipal()).isInstanceOf(CustomAdminDetails.class);
    }

    @Test
    @DisplayName("응답 Body가 null이면 BadCredentialsException을 던진다")
    void authenticate_BodyNull() {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "pw");

        given(authFeignClient.doLogin(any(AdminLoginRequest.class)))
                .willReturn(ResponseEntity.ok().body(null));

        assertThatThrownBy(() -> adminAuthenticationProvider.authenticate(auth))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Access Token을 받지 못했습니다");
    }

    @Test
    @DisplayName("Access Token이 null이면 BadCredentialsException을 던진다")
    void authenticate_AccessTokenNull() {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "pw");
        AccessTokenResponse tokenResponse = new AccessTokenResponse();
        // accessToken 설정 안 함 (null)

        given(authFeignClient.doLogin(any(AdminLoginRequest.class)))
                .willReturn(ResponseEntity.ok().body(tokenResponse));

        assertThatThrownBy(() -> adminAuthenticationProvider.authenticate(auth))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Access Token을 받지 못했습니다");
    }

    @Test
    @DisplayName("Refresh Token 쿠키가 없으면 BadCredentialsException을 던진다")
    void authenticate_CookieNull() {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "pw");
        AccessTokenResponse tokenResponse = new AccessTokenResponse();
        tokenResponse.setAccessToken("token");

        // 헤더 설정 안 함 (null)
        given(authFeignClient.doLogin(any(AdminLoginRequest.class)))
                .willReturn(ResponseEntity.ok().body(tokenResponse));

        assertThatThrownBy(() -> adminAuthenticationProvider.authenticate(auth))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Refresh Token 쿠키를 받지 못했습니다");
    }

    @Test
    @DisplayName("관리자 로그인 중복(409) 발생 시 ConcurrentLoginException을 던진다")
    void authenticate_Conflict() {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "pw");

        FeignException.Conflict mockException = mock(FeignException.Conflict.class);
        given(mockException.status()).willReturn(409);

        given(authFeignClient.doLogin(any(AdminLoginRequest.class))).willThrow(mockException);

        assertThatThrownBy(() -> adminAuthenticationProvider.authenticate(auth))
                .isInstanceOf(ConcurrentLoginException.class);
    }

    @Test
    @DisplayName("관리자 로그인 기타 실패 시 BadCredentialsException을 던진다")
    void authenticate_OtherFailure() {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "pw");

        FeignException.BadRequest mockException = mock(FeignException.BadRequest.class);
        given(mockException.status()).willReturn(400);

        given(authFeignClient.doLogin(any(AdminLoginRequest.class)))
                .willThrow(mockException);

        assertThatThrownBy(() -> adminAuthenticationProvider.authenticate(auth))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("토큰 디코딩 실패 시 BadCredentialsException을 던진다")
    void authenticate_DecodeError() {
        Authentication auth = new UsernamePasswordAuthenticationToken("admin", "pw");

        given(authFeignClient.doLogin(any(AdminLoginRequest.class)))
                .willThrow(new JWTDecodeException("Invalid Token"));

        assertThatThrownBy(() -> adminAuthenticationProvider.authenticate(auth))
                .isInstanceOf(BadCredentialsException.class);
    }

    @Test
    @DisplayName("supports 메서드는 UsernamePasswordAuthenticationToken을 지원한다")
    void supports() {
        assertThat(adminAuthenticationProvider.supports(UsernamePasswordAuthenticationToken.class)).isTrue();
        assertThat(adminAuthenticationProvider.supports(String.class)).isFalse();
    }
}