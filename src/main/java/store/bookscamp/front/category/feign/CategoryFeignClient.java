package store.bookscamp.front.category.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import store.bookscamp.front.category.controller.response.CategoryListResponse;

@FeignClient(name = "category", url = "${gateway.base-url}")
public interface CategoryFeignClient {

    @GetMapping("/api-server/categories")
    List<CategoryListResponse> getAllCategories();

    @DeleteMapping("/api-server/admin/category/delete/{id}")
    void deleteCategory(@PathVariable Long id);

}
