package store.bookscamp.front.admin.controller;

import feign.FeignException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import store.bookscamp.front.admin.controller.request.AdminLoginRequest;
import store.bookscamp.front.admin.repository.AdminLoginFeignClient;
import store.bookscamp.front.member.controller.request.MemberLoginRequest;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminLoginFeignClient adminLoginFeignClient;

    public AdminController(AdminLoginFeignClient adminLoginFeignClient) {
        this.adminLoginFeignClient = adminLoginFeignClient;
    }

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/admin";
        }
        return "admin/login";
    }

    @PostMapping("/login")
    public String doLogin(@Valid AdminLoginRequest adminLoginRequest, HttpServletResponse response) {
        try {
            ResponseEntity<Void> responseEntity = adminLoginFeignClient.doLogin(adminLoginRequest);

            String jwtToken = responseEntity.getHeaders().getFirst("X-Auth-Token");
            Cookie cookie = new Cookie("Authorization", jwtToken);
            response.addCookie(cookie);
            return "redirect:/admin";

        } catch (FeignException e) {
            return "redirect:/admin/login?error";
        }
    }

    @GetMapping
    public String adminIndex() {
        return "admin/index";
    }

    @GetMapping("/dashboard")
    public String adminDashBoard() {
        return "admin/dashboard";
    }
}
