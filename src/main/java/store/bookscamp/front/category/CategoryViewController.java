package store.bookscamp.front.category;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.client.RestTemplate;

@Controller
public class CategoryViewController {

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String BACKEND_URL = "http://localhost:8081"; // 백엔드 서버 주소

    @GetMapping("/sidebar")
    public String sidebar(Model model) {
        List<Map<String, Object>> categories =
                restTemplate.getForObject(BACKEND_URL + "/categories", List.class);
        model.addAttribute("categories", categories);
        return "fragments/sidebar"; // sidebar.html
    }
}