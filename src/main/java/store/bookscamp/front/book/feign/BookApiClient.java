package store.bookscamp.front.book.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import store.bookscamp.front.book.controller.dto.request.BookCreateRequest;
import store.bookscamp.front.common.config.FeignConfig;

@FeignClient(
        name = "api-through-gateway-book",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface BookApiClient {

    @PostMapping(value ="/api-server/admin/books/create",consumes = "application/json")
    void createBook(@RequestBody BookCreateRequest request);
}
