package store.bookscamp.front.category.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import store.bookscamp.front.category.controller.request.CategoryCreateRequest;
import store.bookscamp.front.category.controller.request.CategoryUpdateRequest;
import store.bookscamp.front.category.controller.response.CategoryListResponse;

import java.util.List;

@FeignClient(name = "category", url = "${gateway.base-url}")
public interface CategoryFeignClient {

    @GetMapping("/api-server/categories")
    List<CategoryListResponse> getAllCategories();

    @PostMapping("/api-server/admin/category/create")
    ResponseEntity<Void> createCategory(@RequestBody CategoryCreateRequest request);

    @PutMapping("/api-server/admin/category/update/{id}")
    ResponseEntity<Void> updateCategory(@PathVariable Long id, @RequestBody CategoryUpdateRequest request);


    @DeleteMapping("/api-server/admin/category/delete/{id}")
    ResponseEntity<Void> deleteCategory(@PathVariable Long id);
}
