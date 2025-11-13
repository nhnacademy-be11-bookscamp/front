package store.bookscamp.front.book.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.book.controller.request.AladinCreateRequest;
import store.bookscamp.front.book.controller.request.BookCreateRequest;
import store.bookscamp.front.book.controller.request.BookUpdateRequest;
import store.bookscamp.front.book.controller.response.BookIndexResponse;
import store.bookscamp.front.book.controller.response.BookInfoResponse;
import store.bookscamp.front.book.controller.response.BookSortResponse;
import store.bookscamp.front.book.controller.response.BookWishListResponse;
import store.bookscamp.front.common.pagination.RestPageImpl;
import store.bookscamp.front.common.config.FeignConfig;

import java.time.LocalDate;

@FeignClient(
        name = "api-through-gateway-book",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface BookFeignClient {

    @PutMapping(value = "/api-server/admin/books/{id}/update", produces = "application/json")
    void updateBook(
            @PathVariable Long id,
            @RequestBody BookUpdateRequest request,
            @RequestParam LocalDate publishDate
    );

    @PostMapping(value ="/api-server/admin/books/create", produces = "application/json")
    void createBook(
            @RequestBody BookCreateRequest request,
            @RequestParam LocalDate publishDate
    );

    @PostMapping(value ="/api-server/admin/aladin/books", produces = "application/json")
    void createAladinBook(@RequestBody AladinCreateRequest request, @RequestParam List<String> imgUrls);

    @GetMapping("/api-server/books")
    ResponseEntity<RestPageImpl<BookSortResponse>> getBooks(
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(required = false) String keyWord,
            @RequestParam(value = "sortType", required = false) String sortType,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    );

    @GetMapping("/api-server/bookDetail/{id}")
    BookInfoResponse getBookDetail(@PathVariable Long id);

    @GetMapping("/api-server/books/indexBooks")
    ResponseEntity<List<BookIndexResponse>> getRecommendBooks();

    @GetMapping("/api-server/wishlist")
    ResponseEntity<RestPageImpl<BookWishListResponse>> getWishListBooks();
}
