package store.bookscamp.front.rank.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import store.bookscamp.front.common.config.FeignConfig;
import store.bookscamp.front.rank.controller.request.RankGetRequest;

@FeignClient(
        name = "rank",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface RankFeignClient {

    @GetMapping("/api-server/rank")
    ResponseEntity<RankGetRequest> getRank();
}
