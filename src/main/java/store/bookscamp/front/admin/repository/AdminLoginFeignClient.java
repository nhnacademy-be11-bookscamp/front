package store.bookscamp.front.admin.repository;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import store.bookscamp.front.admin.controller.request.AdminLoginRequest;
import store.bookscamp.front.member.controller.request.MemberLoginRequest;

@FeignClient(name = "adminLoginClient", url = "${gateway.base-url}")
public interface AdminLoginFeignClient {

    @PostMapping("/auth-server/admin/login")
    public ResponseEntity<Void> doLogin(@Valid @RequestBody AdminLoginRequest adminLoginRequest);

}

