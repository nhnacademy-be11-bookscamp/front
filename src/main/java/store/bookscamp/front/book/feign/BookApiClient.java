package store.bookscamp.front.book.feign;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import store.bookscamp.front.book.controller.dto.request.BookRegisterRequest;
import store.bookscamp.front.common.config.FeignConfig;

@FeignClient(
        name = "api-through-gateway-book",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface BookApiClient {

    @PostMapping(value ="/api-server/book/register",consumes = "application/json")
    void registerBook(@RequestBody BookRegisterRequest request);
}
