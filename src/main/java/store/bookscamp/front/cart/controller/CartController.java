package store.bookscamp.front.cart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartController {

    @GetMapping("/carts")
    public String viewCart() {
        return "cart/cart";
    }
}


