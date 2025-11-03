package store.bookscamp.front.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin/category")
public class CategoryController {

    @Value("${gateway.base-url}")
    private String pathPrefix;

    @GetMapping
    public String category(Model model){
        model.addAttribute("apiPrefix", pathPrefix);
        return "admin/category";
    }
}
