package store.bookscamp.front.book;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.book.dto.BookSortResponse;
import store.bookscamp.front.book.dto.RestPageImpl;
import store.bookscamp.front.common.ApiClient;

@Controller
@RequiredArgsConstructor
public class BookViewController {

    private final ApiClient apiClient;

    @GetMapping("/books")
    public String listBook(
            // (5) 브라우저로부터 모든 파라미터를 받습니다.
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyWord,
            @RequestParam(defaultValue = "id") String sortType,
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            Model model
    ) {
        // (6) Feign Client 호출 시 파라미터를 전달합니다.
        // pageable에서 page 번호와 size를 꺼내서 전달합니다.
        ResponseEntity<RestPageImpl<BookSortResponse>> response = apiClient.getBooks(
                categoryId,
                keyWord,
                sortType,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        // (7) ResponseEntity에서 Body(RestPageImpl)를 꺼냅니다.
        // RestPageImpl은 Page의 구현체이므로 Page 타입으로 받을 수 있습니다.
        Page<BookSortResponse> booksPage = response.getBody();

        // (8) Thymeleaf에서 사용할 수 있도록 "booksPage"라는 이름으로 모델에 추가
        model.addAttribute("booksPage", booksPage);

        // (9) (선택사항) 페이징 처리를 위해 현재 파라미터들도 모델에 추가
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("keyWord", keyWord);
        model.addAttribute("sortType", sortType);

        return "books/list"; // (10) 세미콜론 제거
    }
}
