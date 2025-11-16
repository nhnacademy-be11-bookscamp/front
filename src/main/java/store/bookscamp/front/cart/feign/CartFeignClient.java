package store.bookscamp.front.cart.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import store.bookscamp.front.cart.controller.request.CartItemAddRequest;
import store.bookscamp.front.cart.controller.request.CartItemUpdateRequest;
import store.bookscamp.front.cart.controller.response.CartItemsResponse;
import store.bookscamp.front.common.config.FeignConfig;

@FeignClient(
        name = "api-through-gateway-cart",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface CartFeignClient {

    @GetMapping("/api-server/carts")
    ResponseEntity<List<CartItemsResponse>> getCartItems();

    @PostMapping("/api-server/carts")
    ResponseEntity<Void> addCartItems(CartItemAddRequest request);

    @PutMapping("/api-server/carts/{cartItemId}")
    ResponseEntity<Void> updateCartItem(@PathVariable Long cartItemId, CartItemUpdateRequest request);

    @DeleteMapping("/api-server/carts/{cartItemId}")
    ResponseEntity<Void> deleteCartItem(@PathVariable Long cartItemId);

    @DeleteMapping("/api-server/carts")
    ResponseEntity<Void> clearCart();
}
