package store.bookscamp.front.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import store.bookscamp.front.category.feign.CategoryFeignClient;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/category")
public class CategoryController {

    @Value("${gateway.base-url}")
    private String pathPrefix;

    private final CategoryFeignClient categoryFeignClient;

    @GetMapping
    public String category(Model model){
        model.addAttribute("apiPrefix", pathPrefix);
        return "category/category";
    }

    @DeleteMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id){
        categoryFeignClient.deleteCategory(id);
        return "redirect:/category/category";
    }
}
