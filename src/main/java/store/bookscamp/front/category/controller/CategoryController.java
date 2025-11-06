package store.bookscamp.front.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import store.bookscamp.front.category.feign.CategoryFeignClient;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryController {

    private final CategoryFeignClient categoryFeignClient;

    @Value("${gateway.base-url}")
    private String pathPrefix;

    @GetMapping
    public String category(Model model){
        model.addAttribute("apiPrefix", pathPrefix);

        return "admin/category";
    }
}
