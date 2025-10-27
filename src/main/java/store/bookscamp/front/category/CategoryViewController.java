package store.bookscamp.front.category;

import java.util.List;
import lombok.RequiredArgsConstructor; // (1)
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import store.bookscamp.front.category.dto.CategoryListResponse;
import store.bookscamp.front.category.feign.CategoryFeignClient;

@Controller
@RequiredArgsConstructor // (1) Feign Client 주입을 위해
public class CategoryViewController {

    // (2) RestTemplate 대신 Feign Client 인터페이스를 주입
    private final CategoryFeignClient categoryFeignClient;

    /**
     * "http://[프론트서버주소]/sidebar" 로 접속하면
     * Feign Client가 백엔드 API를 호출하여 데이터를 모델에 담고 HTML을 렌더링합니다.
     */
    @GetMapping("/") // (3) 프론트엔드 자신의 URL
    public String sidebar(Model model) {

        try {
            // (4) Feign Client 호출 (마치 로컬 서비스 메서드처럼)
            List<CategoryListResponse> categories = categoryFeignClient.getAllCategories();

            // (5) 받아온 데이터를 Model에 추가
            model.addAttribute("categories", categories);

        } catch (Exception e) {
            // API 호출 실패 시 에러 처리
            model.addAttribute("categories", List.of()); // 빈 리스트 전달
            System.err.println("API 호출 실패: " + e.getMessage());
        }

        // (6) 이 데이터를 fragments/sidebar.html 에 전달하여 렌더링
        return "layouts/default";
    }
}