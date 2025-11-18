package store.bookscamp.front.auth.service;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate; // [수정] Import
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import store.bookscamp.front.admin.repository.AdminLoginFeignClient;
import store.bookscamp.front.auth.dto.AccessTokenResponse;
import store.bookscamp.front.auth.dto.LoginAuthDetails;
import store.bookscamp.front.auth.dto.OauthLoginRequest;
import store.bookscamp.front.auth.user.CustomMemberDetails;
import store.bookscamp.front.member.controller.MemberFeignClient;

import java.util.UUID;
import java.util.List;
import java.util.Map;
import java.util.HashMap; // [수정] Import

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final MemberFeignClient memberFeignClient;
    private final AdminLoginFeignClient adminLoginFeignClient;
    private final RestTemplate restTemplate;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        String provider = userRequest.getClientRegistration().getRegistrationId();
        Map<String, Object> attributes;
        String userNameAttributeName;

        if (provider.equals("google")) {
            log.debug("Google 로그인 요청. super.loadUser() 호출");
            OAuth2User oAuth2User = super.loadUser(userRequest);
            attributes = oAuth2User.getAttributes();
            userNameAttributeName = "sub";

        } else if (provider.equals("payco")) {
            log.debug("Payco 로그인 요청. 커스텀 POST 로직 실행");
            attributes = getPaycoMemberInfo(userRequest);
            userNameAttributeName = "idNo";

        } else {
            log.error("지원하지 않는 OAuth Provider입니다: {}", provider);
            throw new OAuth2AuthenticationException("지원하지 않는 OAuth Provider입니다.");
        }

        String providerId = String.valueOf(attributes.get(userNameAttributeName));
        String username = provider + "_" + providerId;

        try {
            memberFeignClient.checkIdDuplicate(username);
            log.info("OAuth2: 사용 가능한 ID (200 OK). 신규 회원 가입을 시작합니다.");

            return processNewUser(username, provider, attributes, userNameAttributeName);

        } catch (FeignException.Conflict e) {
            log.info("OAuth2: 이미 가입된 회원 (409 Conflict). 기존 회원 로그인을 시작합니다.");

            return processExistingUser(username, attributes, userNameAttributeName);

        } catch (Exception e) {
            log.error("OAuth2 DB 확인 중 알 수 없는 오류 발생", e);
            throw new OAuth2AuthenticationException("OAuth2 로그인 중 DB 오류가 발생했습니다.");
        }
    }

    private Map<String, Object> getPaycoMemberInfo(OAuth2UserRequest userRequest) {
        String userInfoUri = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUri();
        String accessToken = userRequest.getAccessToken().getTokenValue();
        String clientId = userRequest.getClientRegistration().getClientId();

        HttpHeaders headers = new HttpHeaders();
        headers.set("client_id", clientId);
        headers.set("access_token", accessToken);

        headers.set("Content-Type", "application/json");

        String emptyJsonBody = "{}";
        HttpEntity<String> entity = new HttpEntity<>(emptyJsonBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(
                    userInfoUri,
                    HttpMethod.POST,
                    entity,
                    Map.class
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null) {
                throw new OAuth2AuthenticationException("Payco 사용자 정보 응답이 null입니다.");
            }

            if (!responseBody.containsKey("header")) {
                throw new OAuth2AuthenticationException("Payco 응답에 'header' 필드가 없습니다.");
            }
            Map<String, Object> header = (Map<String, Object>) responseBody.get("header");
            Object isSuccessfulValue = header.get("isSuccessful");

            if (!(isSuccessfulValue instanceof Boolean) || !((Boolean) isSuccessfulValue)) {
                String msg = (String) header.get("resultMessage");
                log.warn("Payco API가 오류를 반환했습니다 (isSuccessful=false 또는 missing): {}", msg);
                throw new OAuth2AuthenticationException("Payco API가 오류를 반환했습니다: " + msg);
            }

            if (!responseBody.containsKey("data")) {
                throw new OAuth2AuthenticationException("Payco 응답에 'data' 필드가 없습니다.");
            }
            Map<String, Object> data = (Map<String, Object>) responseBody.get("data");

            if (!data.containsKey("member")) {
                throw new OAuth2AuthenticationException("Payco 응답에 'data.member' 필드가 없습니다.");
            }

            return (Map<String, Object>) data.get("member");

        } catch (Exception e) {
            log.error("Payco 사용자 정보 조회 중 치명적 오류 발생", e);
            throw new OAuth2AuthenticationException("Payco 사용자 정보 조회 중 오류가 발생했습니다: " + e.getMessage());
        }
    }


    private OAuth2User processNewUser(String username, String provider, Map<String, Object> attributes, String userNameAttributeName) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        HttpSession session = request.getSession();

        session.setAttribute("oauth_username", username);

        if (provider.equals("google")) {
            session.setAttribute("oauth_name", attributes.get("name"));
            session.setAttribute("oauth_email", attributes.get("email"));
            session.setAttribute("oauth_mobile", null);
            session.setAttribute("oauth_birthday", null);

        } else if (provider.equals("payco")) {
            session.setAttribute("oauth_name", attributes.get("name"));
            session.setAttribute("oauth_email", attributes.get("email"));
            session.setAttribute("oauth_mobile", attributes.get("mobile"));
            session.setAttribute("oauth_birthday", attributes.get("birthdayMMdd"));
        }

        String randomPassword = UUID.randomUUID().toString();
        session.setAttribute("oauth_password_hash", randomPassword);

        log.info("OAuth2: 신규 회원. 세션에 임시 정보 저장 후 GUEST 권한 부여.");
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_GUEST"));

        return new DefaultOAuth2User(authorities, attributes, userNameAttributeName);
    }


    private OAuth2User processExistingUser(String username, Map<String, Object> attributes, String userNameAttributeName) {
        OauthLoginRequest loginRequest = new OauthLoginRequest(username);
        ResponseEntity<AccessTokenResponse> authResponse;

        try {
            authResponse = adminLoginFeignClient.oauthLogin(loginRequest);
        } catch (Exception e) {
            log.error("OAuth2 기존 회원 로그인(토큰 발급) 실패", e);
            throw new OAuth2AuthenticationException("서버 오류로 토큰 발급에 실패했습니다.");
        }

        AccessTokenResponse body = authResponse.getBody();
        if (body == null) {
            throw new OAuth2AuthenticationException("Auth 서버로부터 AccessTokenResponse body를 받지 못했습니다.");
        }

        String rawJwtToken = "Bearer " + body.getAccessToken();
        String rtCookieString = authResponse.getHeaders().getFirst("Set-Cookie");
        String name = body.getName();

        Map<String, Object> mutableAttributes = new HashMap<>(attributes);

        mutableAttributes.put("rawAccessToken", rawJwtToken);
        mutableAttributes.put("rtCookieString", rtCookieString);
        mutableAttributes.put("name", name);

        log.info("OAuth2: 기존 회원. 토큰 발급 및 SecurityContext에 인증 정보 저장 완료.");

        return new DefaultOAuth2User(
                List.of(new SimpleGrantedAuthority("ROLE_USER")),
                mutableAttributes,
                userNameAttributeName
        );
    }
}