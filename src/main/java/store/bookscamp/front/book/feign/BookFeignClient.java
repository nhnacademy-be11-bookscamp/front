package store.bookscamp.front.book.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.book.dto.BookSortResponse;
import store.bookscamp.front.book.dto.RestPageImpl;

@FeignClient(name = "book", url = "http://localhost:8080")
public interface BookFeignClient {
    @GetMapping("/api-server/books")
    ResponseEntity<RestPageImpl<BookSortResponse>> getBooks(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "keyWord", required = false) String keyWord,
            @RequestParam(value = "sortType", required = false) String sortType,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    );
}
