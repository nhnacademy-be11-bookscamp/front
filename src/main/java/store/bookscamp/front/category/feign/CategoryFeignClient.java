package store.bookscamp.front.category.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import store.bookscamp.front.category.controller.response.CategoryListResponse;

import java.util.List;

@FeignClient(name = "category", url = "${gateway.base-url}")
public interface CategoryFeignClient {

    @GetMapping("/api-server/categories")
    List<CategoryListResponse> getAllCategories();
}
