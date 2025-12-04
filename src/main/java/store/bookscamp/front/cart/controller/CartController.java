package store.bookscamp.front.cart.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import store.bookscamp.front.cart.controller.request.CartItemAddRequest;
import store.bookscamp.front.cart.controller.request.CartItemUpdateRequest;
import store.bookscamp.front.cart.controller.response.CartItemsResponse;
import store.bookscamp.front.cart.feign.CartFeignClient;

@Slf4j
@Controller
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartFeignClient cartFeignClient;

    @GetMapping
    public String viewCart(Model model, HttpServletResponse servletResponse) {

        ResponseEntity<List<CartItemsResponse>> response = cartFeignClient.getCartItems();
        setCookieFromApi(servletResponse, response);

        List<CartItemsResponse> cartItems = response.getBody();
        int total = (cartItems == null) ? 0 : cartItems.stream()
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
    public ResponseEntity<Void> clearCart() {
        ResponseEntity<Void> response = cartFeignClient.clearCart();
        return ResponseEntity.status(response.getStatusCode()).build();
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


