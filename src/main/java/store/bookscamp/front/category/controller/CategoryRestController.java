package store.bookscamp.front.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import store.bookscamp.front.category.controller.request.CategoryCreateRequest;
import store.bookscamp.front.category.controller.request.CategoryUpdateRequest;
import store.bookscamp.front.category.feign.CategoryFeignClient;

@RestController
@RequiredArgsConstructor
public class CategoryRestController {

    private final CategoryFeignClient categoryFeignClient;

    @PostMapping("/api-server/admin/category/create")
    public void createCategory(@RequestBody CategoryCreateRequest request){
        categoryFeignClient.createCategory(request);
    }

    @PostMapping("/api-server/admin/category/update/{id}")
    public void updateCategory(
            @PathVariable Long id,
            @RequestBody CategoryUpdateRequest request
    ){
        categoryFeignClient.updateCategory(id, request);
    }


    @PostMapping("/api-server/admin/category/delete/{id}")
    public void deleteCategory(@PathVariable Long id){
        categoryFeignClient.deleteCategory(id);
    }
}
