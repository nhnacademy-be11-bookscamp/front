package store.bookscamp.front.book.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.book.controller.dto.response.AladinBookResponse;
import store.bookscamp.front.book.controller.dto.response.BookDetailResponse;
import store.bookscamp.front.common.config.FeignConfig;


@FeignClient(
        name = "api-through-gateway-aladin",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface AladinFeignClient {

    @GetMapping("/api-server/admin/aladin/search")
    AladinBookResponse search(
            @RequestParam("query") String query,
            @RequestParam(name = "queryType", required = false) String queryType,
            @RequestParam(name = "start", required = false, defaultValue = "1") Integer start,
            @RequestParam(name = "maxResults", required = false, defaultValue = "10") Integer maxResults,
            @RequestParam(name = "sort", required = false) String sort
    );

    @GetMapping("/api-server/admin/aladin/books/{isbn13}")
    BookDetailResponse getBookDetail(@PathVariable String isbn13);
}