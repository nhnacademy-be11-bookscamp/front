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
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import store.bookscamp.front.auth.dto.AccessTokenResponse;
import store.bookscamp.front.auth.repository.AuthFeignClient;
import store.bookscamp.front.common.exception.ConcurrentLoginException;
import store.bookscamp.front.member.controller.request.MemberLoginRequest;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class CustomAuthenticationProviderTest {

    @Mock
    private AuthFeignClient authFeignClient;

    @InjectMocks
    private CustomAuthenticationProvider authenticationProvider;

    @Test
    @DisplayName("로그인 성공 시 Authentication 객체를 반환한다")
    void authenticate_Success() {
        String username = "user";
        String password = "pw";
        Authentication auth = new UsernamePasswordAuthenticationToken(username, password);

        AccessTokenResponse tokenResponse = new AccessTokenResponse();
        tokenResponse.setAccessToken("access-token");
        tokenResponse.setName("User");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "refresh-token=abc");

        given(authFeignClient.doLogin(any(MemberLoginRequest.class)))
                .willReturn(ResponseEntity.ok().headers(headers).body(tokenResponse));

        Authentication result = authenticationProvider.authenticate(auth);

        assertThat(result).isNotNull();
        assertThat(result.isAuthenticated()).isTrue();
    }

    @Test
    @DisplayName("Feign 응답 Body가 null이면 BadCredentialsException을 던진다")
    void authenticate_ThrowsException_WhenBodyIsNull() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "pw");

        given(authFeignClient.doLogin(any(MemberLoginRequest.class)))
                .willReturn(ResponseEntity.ok().body(null));

        assertThatThrownBy(() -> authenticationProvider.authenticate(auth))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Access Token을 받지 못했습니다");
    }

    @Test
    @DisplayName("Access Token이 null이면 BadCredentialsException을 던진다")
    void authenticate_AccessTokenNull() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "pw");
        AccessTokenResponse tokenResponse = new AccessTokenResponse();
        // accessToken null

        given(authFeignClient.doLogin(any(MemberLoginRequest.class)))
                .willReturn(ResponseEntity.ok().body(tokenResponse));

        assertThatThrownBy(() -> authenticationProvider.authenticate(auth))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Access Token을 받지 못했습니다");
    }

    @Test
    @DisplayName("Refresh Token 쿠키가 없으면 BadCredentialsException을 던진다")
    void authenticate_CookieNull() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "pw");
        AccessTokenResponse tokenResponse = new AccessTokenResponse();
        tokenResponse.setAccessToken("token");
        // 헤더 없음

        given(authFeignClient.doLogin(any(MemberLoginRequest.class)))
                .willReturn(ResponseEntity.ok().body(tokenResponse));

        assertThatThrownBy(() -> authenticationProvider.authenticate(auth))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Refresh Token 쿠키를 받지 못했습니다");
    }

    @Test
    @DisplayName("휴면 회원(Dormant)일 경우 DisabledException을 던진다")
    void authenticate_ThrowsDisabledException_WhenDormantMember() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "pw");

        FeignException.Unauthorized mockException = mock(FeignException.Unauthorized.class);

        given(mockException.status()).willReturn(401);

        Map<String, Collection<String>> headers = Map.of(
                "x-auth-error-code", Collections.singletonList("DORMANT_MEMBER")
        );
        given(mockException.responseHeaders()).willReturn(headers);

        given(authFeignClient.doLogin(any(MemberLoginRequest.class))).willThrow(mockException);

        assertThatThrownBy(() -> authenticationProvider.authenticate(auth))
                .isInstanceOf(DisabledException.class)
                .hasMessage("DORMANT_MEMBER");
    }

    @Test
    @DisplayName("401 에러지만 휴면 회원이 아닌 경우(헤더 없음) 일반 에러로 처리한다")
    void authenticate_401_NotDormant() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "pw");

        FeignException.Unauthorized mockException = mock(FeignException.Unauthorized.class);
        given(mockException.status()).willReturn(401);
        given(mockException.responseHeaders()).willReturn(Collections.emptyMap()); // 헤더 없음

        given(authFeignClient.doLogin(any(MemberLoginRequest.class))).willThrow(mockException);

        assertThatThrownBy(() -> authenticationProvider.authenticate(auth))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("로그인에 실패했습니다");
    }

    @Test
    @DisplayName("중복 로그인(409 Conflict) 발생 시 ConcurrentLoginException을 던진다")
    void authenticate_ThrowsConcurrentLoginException_WhenConflict() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "pw");

        FeignException.Conflict mockException = mock(FeignException.Conflict.class);
        given(mockException.status()).willReturn(409);

        given(authFeignClient.doLogin(any(MemberLoginRequest.class)))
                .willThrow(mockException);

        assertThatThrownBy(() -> authenticationProvider.authenticate(auth))
                .isInstanceOf(ConcurrentLoginException.class)
                .hasMessage("이미 사용중인 ID입니다.");
    }

    @Test
    @DisplayName("기타 Feign 에러 발생 시 BadCredentialsException을 던진다")
    void authenticate_ThrowsBadCredentialsException_WhenOtherFeignError() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "pw");

        FeignException.BadRequest mockException = mock(FeignException.BadRequest.class);
        given(mockException.status()).willReturn(400);

        given(authFeignClient.doLogin(any(MemberLoginRequest.class)))
                .willThrow(mockException);

        assertThatThrownBy(() -> authenticationProvider.authenticate(auth))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("로그인에 실패했습니다");
    }

    @Test
    @DisplayName("토큰 디코딩 실패 시 BadCredentialsException을 던진다")
    void authenticate_ThrowsBadCredentialsException_WhenJWTDecodeException() {
        Authentication auth = new UsernamePasswordAuthenticationToken("user", "pw");

        given(authFeignClient.doLogin(any(MemberLoginRequest.class)))
                .willThrow(new JWTDecodeException("Decode Error"));

        assertThatThrownBy(() -> authenticationProvider.authenticate(auth))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("토큰 디코딩에 실패했습니다");
    }

    @Test
    @DisplayName("supports 메서드는 UsernamePasswordAuthenticationToken을 지원한다")
    void supports() {
        assertThat(authenticationProvider.supports(UsernamePasswordAuthenticationToken.class)).isTrue();
        assertThat(authenticationProvider.supports(String.class)).isFalse();
    }
}