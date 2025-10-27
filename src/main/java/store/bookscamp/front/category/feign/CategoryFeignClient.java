package store.bookscamp.front.category.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import store.bookscamp.front.category.dto.CategoryListResponse;

@FeignClient(name = "category", url = "http://localhost:8080")
public interface CategoryFeignClient {

    @GetMapping("/api-server/categories")
    List<CategoryListResponse> getAllCategories();
}
