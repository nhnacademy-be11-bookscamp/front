package store.bookscamp.front.admin.repository;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import store.bookscamp.front.admin.controller.request.AdminLoginRequest;
import store.bookscamp.front.auth.dto.AccessTokenResponse;
import store.bookscamp.front.common.config.AuthFeignConfig;

@FeignClient(name = "adminLoginClient", url = "${gateway.base-url}", configuration = AuthFeignConfig.class)
public interface AdminLoginFeignClient {

    @PostMapping("/auth-server/admin/login")
    public ResponseEntity<AccessTokenResponse> doLogin(@Valid @RequestBody AdminLoginRequest adminLoginRequest);

    @PostMapping("/auth-server/reissue")
    ResponseEntity<AccessTokenResponse> reissue(@CookieValue("refresh_token") String refreshToken);

    @PostMapping("/auth-server/logout")
    ResponseEntity<Void> doLogout(@CookieValue("refresh_token") String refreshToken);
}


