package store.bookscamp.front.booklike.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import store.bookscamp.front.booklike.controller.request.BookLikeRequest;
import store.bookscamp.front.booklike.controller.response.BookLikeCountResponse;
import store.bookscamp.front.booklike.controller.response.BookLikeStatusResponse;
import store.bookscamp.front.common.config.FeignConfig;

@FeignClient(
        name = "bookLike",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface BookLikeFeginClient {

    @GetMapping("/api-server/books/{bookId}/like/count")
    ResponseEntity<BookLikeCountResponse> getLikeCount(@PathVariable Long bookId);

    @GetMapping("/api-server/books/{bookId}/like/status")
    ResponseEntity<BookLikeStatusResponse> getLikeStatus(@PathVariable Long bookId);
}
