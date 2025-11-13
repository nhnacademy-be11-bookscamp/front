package store.bookscamp.front.admin.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @GetMapping("/login")
    public String loginPage(Authentication authentication) {

        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/admin";
        }
        return "admin/login";
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
