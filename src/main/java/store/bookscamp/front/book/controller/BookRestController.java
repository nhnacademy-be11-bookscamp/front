package store.bookscamp.front.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import store.bookscamp.front.book.feign.BookFeignClient;

@RestController
@RequiredArgsConstructor
public class BookRestController {

    private final BookFeignClient bookFeignClient;

    @DeleteMapping("/api-server/wishlist/{itemId}")
    public void deleteWishList(@PathVariable Long itemId){
        bookFeignClient.deleteWishList(itemId);
    }
}
