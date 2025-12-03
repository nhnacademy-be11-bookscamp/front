package store.bookscamp.front.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import store.bookscamp.front.book.controller.response.BookCouponResponse;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.common.pagination.RestPageImpl;

@RestController
@RequiredArgsConstructor
public class BookRestController {

    private final BookFeignClient bookFeignClient;

    @PostMapping("/api-server/wishlist/{itemId}")
    public void deleteWishList(@PathVariable Long itemId){
        bookFeignClient.deleteWishList(itemId);
    }

    @GetMapping("/api-server/admin/books/coupon")
    public ResponseEntity<RestPageImpl<BookCouponResponse>> getBooks(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        RestPageImpl<BookCouponResponse> books = bookFeignClient.getBooks(keyword, page, size).getBody();

        return ResponseEntity.ok(books);
    }
}
