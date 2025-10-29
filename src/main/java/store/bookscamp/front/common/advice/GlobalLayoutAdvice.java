package store.bookscamp.front.common.advice;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import store.bookscamp.front.category.dto.CategoryListResponse;
import store.bookscamp.front.category.feign.CategoryFeignClient;

@ControllerAdvice
@RequiredArgsConstructor
public class GlobalLayoutAdvice {

    private final CategoryFeignClient categoryFeignClient;

    @ModelAttribute("categories")
    public List<CategoryListResponse> addCategoriesToModel() {

        return categoryFeignClient.getAllCategories();
    }
}
