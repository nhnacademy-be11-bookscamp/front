package store.bookscamp.front.category.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import store.bookscamp.front.category.dto.CategoryListResponse;
import store.bookscamp.front.category.feign.CategoryFeignClient;

@ControllerAdvice
@RequiredArgsConstructor
public class CategoryLayoutAdvice {

    private final CategoryFeignClient categoryFeignClient;

    @ModelAttribute("categories")
    public List<CategoryListResponse> addCategoriesToModel() {

        return categoryFeignClient.getAllCategories();
    }
}
