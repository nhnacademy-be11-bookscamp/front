package store.bookscamp.front.book.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;
import store.bookscamp.front.book.controller.request.AladinCreateRequest;
import store.bookscamp.front.book.controller.request.BookCreateRequest;
import store.bookscamp.front.book.controller.request.BookUpdateRequest;
import store.bookscamp.front.book.controller.response.BookInfoResponse;
import store.bookscamp.front.book.controller.response.BookSortResponse;
import store.bookscamp.front.common.pagination.RestPageImpl;
import store.bookscamp.front.common.config.FeignConfig;

import java.time.LocalDate;
import java.util.List;

@FeignClient(
        name = "api-through-gateway-book",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface BookFeignClient {

    @PostMapping(value = "/api-server/admin/books/{id}/update", consumes = {"multipart/form-data"})
    void updateBook(
            @PathVariable Long id,
            @RequestPart("request") BookUpdateRequest request,
            @RequestParam LocalDate publishDate,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    );

    @PostMapping(value ="/api-server/admin/books/create",consumes = {"multipart/form-data"})
    void createBook(
            @RequestPart("request") BookCreateRequest request,
            @RequestParam LocalDate publishDate,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    );

    @PostMapping(value ="/api-server/admin/aladin/books",consumes = "application/json")
    void createAladinBook(@RequestBody AladinCreateRequest request);

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
