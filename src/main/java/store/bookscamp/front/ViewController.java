package store.bookscamp.front;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import store.bookscamp.front.category.dto.CategoryListResponse;
import store.bookscamp.front.category.feign.CategoryFeignClient;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final CategoryFeignClient categoryFeignClient;


    @GetMapping("/")
    public String index(Model model) {
        try {
            List<CategoryListResponse> categories = categoryFeignClient.getAllCategories();
            model.addAttribute("categories", categories);

        } catch (Exception e) {
            model.addAttribute("categories", List.of()); // 빈 리스트 전달
            System.err.println("API 호출 실패: " + e.getMessage());
        }
        return "index";
    }

}
