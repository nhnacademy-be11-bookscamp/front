package store.bookscamp.front.pointhistory.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.common.config.FeignConfig;
import store.bookscamp.front.pointhistory.controller.response.PageResponse;
import store.bookscamp.front.pointhistory.controller.response.PointHistoryResponse;

@FeignClient(
        name = "point-history",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface PointHistoryFeignClient {

    @GetMapping("/api-server/member/point-histories")
    ResponseEntity<PageResponse<PointHistoryResponse>> getMyPointHistories(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    );
}
