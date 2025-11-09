package store.bookscamp.front.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/categories")
public class CategoryController {

    @Value("${app.api.prefix}")
    private String apiPrefix;

    @GetMapping
    public String category(Model model){
        model.addAttribute("apiPrefix", apiPrefix);
        return "admin/category";
    }
}
