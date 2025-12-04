package store.bookscamp.front.auth.service;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import store.bookscamp.front.auth.dto.AccessTokenResponse;
import store.bookscamp.front.auth.dto.OauthLoginRequest;
import store.bookscamp.front.auth.repository.AuthFeignClient;
import store.bookscamp.front.member.controller.MemberFeignClient;

import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomOAuth2UserServiceTest {

    @Mock
    private MemberFeignClient memberFeignClient;
    @Mock
    private AuthFeignClient authFeignClient;
    @Mock
    private RestTemplate restTemplate;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpSession session;

    @InjectMocks
    private CustomOAuth2UserService customOAuth2UserService;

    @BeforeEach
    void setUp() {
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(request));
    }

    @Test
    @DisplayName("Payco 신규 회원 로그인 성공 (200 OK)")
    void loadUser_Payco_NewUser() {
        OAuth2UserRequest userRequest = createMockUserRequest("payco");

        Map<String, Object> paycoResponse = Map.of(
                "header", Map.of("isSuccessful", true, "resultMessage", "SUCCESS"),
                "data", Map.of("member", Map.of("idNo", "payco123", "name", "Payco User", "email", "p@test.com"))
        );
        given(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(paycoResponse));

        given(memberFeignClient.checkIdDuplicate(anyString())).willReturn(ResponseEntity.ok().build());
        given(request.getSession()).willReturn(session);

        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        assertThat(result).isNotNull();
        assertThat(result.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_GUEST");

        verify(session).setAttribute("oauth_username", "payco_payco123");
    }

    @Test
    @DisplayName("Payco 기존 회원 로그인 성공 (409 Conflict -> Token 발급)")
    void loadUser_Payco_ExistingUser() {
        OAuth2UserRequest userRequest = createMockUserRequest("payco");

        Map<String, Object> paycoResponse = Map.of(
                "header", Map.of("isSuccessful", true),
                "data", Map.of("member", Map.of("idNo", "payco123", "name", "Payco User"))
        );
        given(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(paycoResponse));

        given(memberFeignClient.checkIdDuplicate(anyString())).willThrow(new FeignException.Conflict("Exist", request(), null, null));

        AccessTokenResponse tokenResponse = new AccessTokenResponse();
        tokenResponse.setAccessToken("access-token");
        tokenResponse.setName("Payco User");

        HttpHeaders headers = new HttpHeaders();
        headers.add("Set-Cookie", "refresh_token=rt");

        given(authFeignClient.oauthLogin(any(OauthLoginRequest.class)))
                .willReturn(ResponseEntity.ok().headers(headers).body(tokenResponse));

        OAuth2User result = customOAuth2UserService.loadUser(userRequest);

        assertThat(result.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_USER");
        assertThat(result.getAttributes()).containsEntry("rawAccessToken", "Bearer access-token");
    }

    @Test
    @DisplayName("Payco API 응답 Body가 null이면 예외 발생")
    void loadUser_Payco_BodyNull() {
        OAuth2UserRequest userRequest = createMockUserRequest("payco");

        given(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(null));

        assertThatThrownBy(() -> customOAuth2UserService.loadUser(userRequest))
                .isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    @DisplayName("Payco 응답에 header 필드가 없으면 예외 발생")
    void loadUser_Payco_HeaderMissing() {
        OAuth2UserRequest userRequest = createMockUserRequest("payco");
        Map<String, Object> paycoResponse = Map.of("data", Map.of());

        given(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(paycoResponse));

        assertThatThrownBy(() -> customOAuth2UserService.loadUser(userRequest))
                .isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    @DisplayName("Payco API 응답이 실패(isSuccessful=false)하면 예외 발생")
    void loadUser_Payco_ApiFail() {
        OAuth2UserRequest userRequest = createMockUserRequest("payco");
        Map<String, Object> paycoResponse = Map.of(
                "header", Map.of("isSuccessful", false, "resultMessage", "Fail")
        );
        given(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(paycoResponse));

        assertThatThrownBy(() -> customOAuth2UserService.loadUser(userRequest))
                .isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    @DisplayName("Payco 응답에 data 필드가 없으면 예외 발생")
    void loadUser_Payco_DataMissing() {
        OAuth2UserRequest userRequest = createMockUserRequest("payco");
        Map<String, Object> paycoResponse = Map.of(
                "header", Map.of("isSuccessful", true)
        );

        given(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(paycoResponse));

        assertThatThrownBy(() -> customOAuth2UserService.loadUser(userRequest))
                .isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    @DisplayName("Payco 응답 data에 member 필드가 없으면 예외 발생")
    void loadUser_Payco_MemberMissing() {
        OAuth2UserRequest userRequest = createMockUserRequest("payco");
        Map<String, Object> paycoResponse = Map.of(
                "header", Map.of("isSuccessful", true),
                "data", Map.of("foo", "bar")
        );

        given(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(paycoResponse));

        assertThatThrownBy(() -> customOAuth2UserService.loadUser(userRequest))
                .isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    @DisplayName("지원하지 않는 Provider 요청 시 예외 발생")
    void loadUser_UnknownProvider() {
        OAuth2UserRequest userRequest = createMockUserRequest("naver");

        assertThatThrownBy(() -> customOAuth2UserService.loadUser(userRequest))
                .isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    @DisplayName("DB 확인 중 알 수 없는 오류 발생 시 예외 발생")
    void loadUser_DBError() {
        OAuth2UserRequest userRequest = createMockUserRequest("payco");
        Map<String, Object> paycoResponse = Map.of(
                "header", Map.of("isSuccessful", true),
                "data", Map.of("member", Map.of("idNo", "payco123"))
        );
        given(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(paycoResponse));

        given(memberFeignClient.checkIdDuplicate(anyString())).willThrow(new RuntimeException("DB Down"));

        assertThatThrownBy(() -> customOAuth2UserService.loadUser(userRequest))
                .isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    @DisplayName("기존 회원 로그인 시 Auth 서버 통신 오류 발생")
    void loadUser_ExistingUser_AuthServerFail() {
        OAuth2UserRequest userRequest = createMockUserRequest("payco");
        Map<String, Object> paycoResponse = Map.of(
                "header", Map.of("isSuccessful", true),
                "data", Map.of("member", Map.of("idNo", "payco123"))
        );
        given(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(paycoResponse));

        given(memberFeignClient.checkIdDuplicate(anyString())).willThrow(new FeignException.Conflict("Exist", request(), null, null));
        given(authFeignClient.oauthLogin(any(OauthLoginRequest.class))).willThrow(new RuntimeException("Conn Refused"));

        assertThatThrownBy(() -> customOAuth2UserService.loadUser(userRequest))
                .isInstanceOf(OAuth2AuthenticationException.class);
    }

    @Test
    @DisplayName("기존 회원 로그인 시 Auth 서버 응답 Body가 null이면 예외 발생")
    void processExistingUser_BodyNull() {
        OAuth2UserRequest userRequest = createMockUserRequest("payco");
        Map<String, Object> paycoResponse = Map.of(
                "header", Map.of("isSuccessful", true),
                "data", Map.of("member", Map.of("idNo", "payco123"))
        );
        given(restTemplate.exchange(anyString(), eq(HttpMethod.POST), any(HttpEntity.class), any(ParameterizedTypeReference.class)))
                .willReturn(ResponseEntity.ok(paycoResponse));

        given(memberFeignClient.checkIdDuplicate(anyString())).willThrow(new FeignException.Conflict("Exist", request(), null, null));

        given(authFeignClient.oauthLogin(any(OauthLoginRequest.class))).willReturn(ResponseEntity.ok(null));

        assertThatThrownBy(() -> customOAuth2UserService.loadUser(userRequest))
                .isInstanceOf(OAuth2AuthenticationException.class);
    }

    private OAuth2UserRequest createMockUserRequest(String provider) {
        ClientRegistration registration = ClientRegistration.withRegistrationId(provider)
                .clientId("client-id")
                .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE)
                .redirectUri("uri")
                .authorizationUri("https://auth-uri")
                .tokenUri("token-uri")
                .userInfoUri("user-info-uri")
                .userNameAttributeName("id")
                .build();

        OAuth2AccessToken accessToken = new OAuth2AccessToken(OAuth2AccessToken.TokenType.BEARER, "token", null, null);

        return new OAuth2UserRequest(registration, accessToken);
    }

    private feign.Request request() {
        return feign.Request.create(feign.Request.HttpMethod.GET, "url", Collections.emptyMap(), null, null, null);
    }
}