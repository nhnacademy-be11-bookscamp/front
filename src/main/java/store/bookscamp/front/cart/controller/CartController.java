package store.bookscamp.front.cart.controller;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import store.bookscamp.front.cart.controller.request.CartItemAddRequest;
import store.bookscamp.front.cart.controller.request.CartItemUpdateRequest;
import store.bookscamp.front.cart.controller.response.CartItemsResponse;
import store.bookscamp.front.cart.feign.CartFeignClient;
import store.bookscamp.front.order.dto.OrderCreateRequest;
import store.bookscamp.front.order.dto.OrderCreateResponse;
import store.bookscamp.front.order.feign.OrderFeignClient;

@Slf4j
@Controller
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartFeignClient cartFeignClient;
    private final OrderFeignClient orderFeignClient;

    @GetMapping
    public String viewCart(Model model, HttpServletResponse servletResponse) {

        ResponseEntity<List<CartItemsResponse>> response = cartFeignClient.getCartItems();
        setCookieFromApi(servletResponse, response);

        List<CartItemsResponse> cartItems = response.getBody();
        int total = cartItems.stream()
                .mapToInt(CartItemsResponse::totalPrice)
                .sum();

        model.addAttribute("items", cartItems);
        model.addAttribute("total", total);

        return "cart/cart";
    }

    @PostMapping
    public ResponseEntity<Void> addCart(
            @Valid @RequestBody CartItemAddRequest request,
            HttpServletResponse servletResponse
    ) {
        ResponseEntity<Void> response = cartFeignClient.addCartItems(request);
        setCookieFromApi(servletResponse, response);
        return ResponseEntity.status(response.getStatusCode()).build();
    }

    @PostMapping("/{cartItemId}/update")
    public ResponseEntity<Void> updateCartItem(
            @PathVariable Long cartItemId,
            @Valid @RequestBody CartItemUpdateRequest request
    ) {
        ResponseEntity<Void> response = cartFeignClient.updateCartItem(cartItemId, request);
        return ResponseEntity.status(response.getStatusCode()).build();
    }

    @PostMapping("/{cartItemId}/delete")
    public ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId) {
        ResponseEntity<Void> response = cartFeignClient.deleteCartItem(cartItemId);
        return ResponseEntity.status(response.getStatusCode()).build();
    }

    @PostMapping("/clear")
    public ResponseEntity<OrderCreateResponse> clearCart(
            @RequestBody OrderCreateRequest request,
            HttpServletRequest httpRequest
    ) {
        log.info("=== 주문 생성 요청 시작 ===");
        log.info("요청 데이터: {}", request);

        boolean isMember = isAuthenticatedMember(httpRequest);

        if (!isMember && request.nonMemberInfo() == null) {
            log.error("주문 생성 실패: 비회원 정보 누락");
            throw new IllegalArgumentException("비회원 정보는 필수입니다.");
        }

        if (isMember && request.nonMemberInfo() != null) {
            log.error("주문 생성 실패: 회원이 비회원 정보 입력");
            throw new IllegalArgumentException("회원은 비회원 정보를 입력할 수 없습니다.");
        }

        ResponseEntity<OrderCreateResponse> response = orderFeignClient.createOrder(request);

        log.info("주문 생성 응답 상태: {}", response.getStatusCode());
        log.info("주문 생성 응답 데이터: {}", response.getBody());
        log.info("=== 주문 생성 요청 완료 ===");

        return response;
    }

    private boolean isAuthenticatedMember(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            return false;
        }

        return Arrays.stream(cookies)
                .anyMatch(cookie -> "Authorization".equals(cookie.getName())
                        && cookie.getValue() != null
                        && !cookie.getValue().isEmpty());
    }

    private static void setCookieFromApi(HttpServletResponse servletResponse,
                                         ResponseEntity<?> response) {
        List<String> cookies = response.getHeaders().get("Set-Cookie");
        if (cookies != null) {
            for (String cookie : cookies) {
                servletResponse.addHeader("Set-Cookie", cookie);
            }
        }
    }
}


