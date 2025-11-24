package store.bookscamp.front.booklike.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import store.bookscamp.front.booklike.controller.request.BookLikeRequest;
import store.bookscamp.front.booklike.feign.BookLikeFeignClient;

@RestController
@RequiredArgsConstructor
public class BookLikeRestController {

    private final BookLikeFeignClient bookLikeFeignClient;

    @PutMapping("/api-server/books/joa/{bookId}")
    public void toggleLike(
            @PathVariable Long bookId,
            @RequestBody BookLikeRequest request
    ){
        bookLikeFeignClient.toggleLike(bookId, request);
    }
}
