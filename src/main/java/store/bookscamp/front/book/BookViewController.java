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
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.category.dto.CategoryListResponse;
import store.bookscamp.front.category.feign.CategoryFeignClient;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class BookViewController {

    private final BookFeignClient bookFeignClient;
    private final CategoryFeignClient categoryFeignClient; // ✅ 추가

    @GetMapping("/books")
    public String listBook(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) String keyWord,
            @RequestParam(defaultValue = "id") String sortType,
            @PageableDefault(size = 10, page = 0) Pageable pageable,
            Model model
    ) {

        ResponseEntity<RestPageImpl<BookSortResponse>> response = bookFeignClient.getBooks(
                categoryId,
                keyWord,
                sortType,
                pageable.getPageNumber(),
                pageable.getPageSize()
        );

        Page<BookSortResponse> booksPage = response.getBody();
        model.addAttribute("booksPage", booksPage);

        List<CategoryListResponse> categories = categoryFeignClient.getAllCategories();
        model.addAttribute("categories", categories);

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("keyWord", keyWord);
        model.addAttribute("sortType", sortType);

        return "books/list";
    }
}
