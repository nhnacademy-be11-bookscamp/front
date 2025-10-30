package store.bookscamp.front.book.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.book.controller.dto.request.BookCreateRequest;
import store.bookscamp.front.book.controller.dto.response.BookInfoResponse;
import store.bookscamp.front.book.controller.dto.response.BookSortResponse;
import store.bookscamp.front.common.pagination.RestPageImpl;
import store.bookscamp.front.common.config.FeignConfig;

@FeignClient(
        name = "api-through-gateway-book",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface BookFeignClient {

    @PostMapping(value ="/api-server/admin/books/create",consumes = "application/json")
    void createBook(@RequestBody BookCreateRequest request);

    @GetMapping("/api-server/books")
    ResponseEntity<RestPageImpl<BookSortResponse>> getBooks(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "keyWord", required = false) String keyWord,
            @RequestParam(value = "sortType", required = false) String sortType,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    );

    @GetMapping("/api-server/bookDetail/{id}")
    BookInfoResponse getBookDetail(@PathVariable Long id);
}
