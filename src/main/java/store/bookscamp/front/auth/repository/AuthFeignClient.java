package store.bookscamp.front.auth.repository;

import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import store.bookscamp.front.admin.controller.request.AdminLoginRequest;
import store.bookscamp.front.auth.dto.AccessTokenResponse;
import store.bookscamp.front.auth.dto.OauthLoginRequest;
import store.bookscamp.front.common.config.AuthFeignConfig;
import store.bookscamp.front.member.controller.request.MemberLoginRequest;

@FeignClient(name = "authFeignClient", url = "${gateway.base-url}", configuration = AuthFeignConfig.class)
public interface AuthFeignClient {
    @PostMapping("/auth-server/oauth/login")
    ResponseEntity<AccessTokenResponse> oauthLogin(@RequestBody OauthLoginRequest request);

    @PostMapping("/auth-server/reissue")
    ResponseEntity<AccessTokenResponse> reissue(@CookieValue("refresh_token") String refreshToken);

    @PostMapping("/auth-server/logout")
    ResponseEntity<Void> doLogout(@CookieValue("refresh_token") String refreshToken);

    @PostMapping("/auth-server/login")
    ResponseEntity<AccessTokenResponse> doLogin(@Valid @RequestBody MemberLoginRequest memberLoginRequest);

    @PostMapping("/auth-server/admin/login")
    ResponseEntity<AccessTokenResponse> doLogin(@Valid @RequestBody AdminLoginRequest adminLoginRequest);

    @PostMapping("/auth-server/dormant/send")
    ResponseEntity<Map<String, String>> sendDormantCode(@RequestBody Map<String, String> request);

    @PostMapping("/auth-server/dormant/verify")
    ResponseEntity<Map<String, String>> verifyDormantCode(@RequestBody Map<String, String> request);
}
