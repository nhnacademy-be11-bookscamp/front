package store.bookscamp.front.category.advice;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import store.bookscamp.front.category.controller.response.CategoryListResponse;
import store.bookscamp.front.category.service.CategoryService;

@ControllerAdvice
@RequiredArgsConstructor
public class CategoryLayoutAdvice {

    private final CategoryService categoryService;

    @ModelAttribute("categories")
    public List<CategoryListResponse> addCategoriesToModel() {

        return categoryService.getAllCategories();
    }
}
