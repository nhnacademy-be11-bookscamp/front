package store.bookscamp.front.book.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.bookscamp.front.book.controller.request.AladinCreateRequest;
import store.bookscamp.front.book.controller.request.BookCreateRequest;
import store.bookscamp.front.book.controller.response.BookDetailResponse;
import store.bookscamp.front.book.controller.response.BookInfoResponse;
import store.bookscamp.front.book.controller.response.BookSortResponse;
import store.bookscamp.front.common.pagination.RestPageImpl;
import store.bookscamp.front.book.feign.AladinFeignClient;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.category.dto.CategoryListResponse;
import store.bookscamp.front.category.feign.CategoryFeignClient;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final AladinFeignClient aladinFeignClient;
    private final BookFeignClient bookFeignClient;
    private final CategoryFeignClient categoryFeignClient;

    @GetMapping("/admin/books")
    public String adminBooksHome() {
        return "admin/books";
    }

    // 수동 등록

    @GetMapping("/admin/books/new")
    public String showCreatePage(@RequestParam(value = "isbn",required = false) String isbn, Model model) {
        return "book/create";
    }

    @PostMapping("/admin/books")
    public String createBook(@ModelAttribute BookCreateRequest req) {
        bookFeignClient.createBook(req);
        return "redirect:/admin/books";
    }

    // 알라딘 등록

    @GetMapping("/admin/aladin/books")
    public String aladinShowCreateBook(@RequestParam(value = "isbn",required = false) String isbn, Model model) {

        BookDetailResponse detail = aladinFeignClient.getBookDetail(isbn);

        List<CategoryListResponse> categories = categoryFeignClient.getAllCategories();

        model.addAttribute("aladinBook", detail);
        model.addAttribute("categories", categories);
        return "/aladin/create";
    }

    @PostMapping("/admin/aladin/books")
    public String aladinCreateBook(@ModelAttribute AladinCreateRequest req) {
        bookFeignClient.createAladinBook(req);
        return "redirect:/admin/aladin/search";
    }

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

        RestPageImpl<BookSortResponse> booksPage = response.getBody();
        model.addAttribute("booksPage", booksPage);

        List<CategoryListResponse> categories = categoryFeignClient.getAllCategories();
        model.addAttribute("categories", categories);

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("keyWord", keyWord);
        model.addAttribute("sortType", sortType);

        return "book/list";
    }

    @GetMapping("/books/{id}")
    public String bookDetail(
            @PathVariable("id") Long id,
            Model model
    ){

        BookInfoResponse bookDetail = bookFeignClient.getBookDetail(id);
        model.addAttribute("book", bookDetail);

        return "book/detail";
    }
}


