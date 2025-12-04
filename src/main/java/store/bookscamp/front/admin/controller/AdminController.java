package store.bookscamp.front.admin.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.order.dto.OrderDetailResponse;
import store.bookscamp.front.order.dto.OrderListResponse;
import store.bookscamp.front.order.dto.PageResponse;
import store.bookscamp.front.order.feign.OrderFeignClient;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final OrderFeignClient orderFeignClient;

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

    @GetMapping("/orders")
    public String getAdminOrderList(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String sort,
            Model model
    ) {
        ResponseEntity<PageResponse<OrderListResponse>> response =
                orderFeignClient.getAdminOrderList(page, size, sort);

        PageResponse<OrderListResponse> orderPage = response.getBody();

        List<OrderListResponse> orders =
                (orderPage != null) ? orderPage.content() : List.of();

        model.addAttribute("orderPage", orderPage);
        model.addAttribute("orders", orders);
        model.addAttribute("currentPage", page);
        model.addAttribute("pageSize", size);
        model.addAttribute("sort", sort);

        return "admin/order-list";
    }

    @GetMapping("/orders/{orderId}")
    public String getAdminOrderDetail(@PathVariable Long orderId, Model model) {
        ResponseEntity<OrderDetailResponse> response = orderFeignClient.getAdminOrderDetail(orderId);
        model.addAttribute("order", response.getBody());
        model.addAttribute("isAdmin", true);
        return "admin/order-detail";
    }
}
