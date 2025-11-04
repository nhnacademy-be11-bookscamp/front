package store.bookscamp.front.book.controller;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import store.bookscamp.front.book.controller.request.AladinCreateRequest;
import store.bookscamp.front.book.controller.request.BookCreateRequest;
import store.bookscamp.front.book.controller.request.BookUpdateRequest;
import store.bookscamp.front.book.controller.response.BookDetailResponse;
import store.bookscamp.front.book.controller.response.BookInfoResponse;
import store.bookscamp.front.book.controller.response.BookSortResponse;
import store.bookscamp.front.category.controller.response.CategoryListResponse;
import store.bookscamp.front.common.pagination.RestPageImpl;
import store.bookscamp.front.book.feign.AladinFeignClient;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.category.feign.CategoryFeignClient;
import store.bookscamp.front.tag.TagFeignClient;
import store.bookscamp.front.tag.controller.response.TagGetResponse;

@Controller
@RequiredArgsConstructor
public class BookController {

    private final AladinFeignClient aladinFeignClient;
    private final BookFeignClient bookFeignClient;
    private final CategoryFeignClient categoryFeignClient;
    private final TagFeignClient tagFeignClient;

    @GetMapping("/admin/books")
    public String adminBooksHome() {
        return "admin/books";
    }

    // 수동 등록

    @GetMapping("/admin/books/new")
    public String showCreatePage(Model model) {

        List<CategoryListResponse> categories = categoryFeignClient.getAllCategories();
        List<TagGetResponse> tags = tagFeignClient.getAll();

        model.addAttribute("categories", categories);
        model.addAttribute("tags", tags);

        return "book/create";
    }

    @PostMapping(value = "/admin/books", consumes = "multipart/form-data")
    public String createBook(
            @ModelAttribute BookCreateRequest req,
            @RequestPart("files") List<MultipartFile> files
    ) {

        bookFeignClient.createBook(req, req.getPublishDate(), files);

        return "redirect:/admin/books";
    }

    // 알라딘 등록

    @GetMapping("/admin/aladin/books")
    public String ShowAladinCreatePage(@RequestParam(value = "isbn",required = false) String isbn, Model model) {

        BookDetailResponse detail = aladinFeignClient.getBookDetail(isbn);

        List<CategoryListResponse> categories = categoryFeignClient.getAllCategories();
        List<TagGetResponse> tags = tagFeignClient.getAll();

        model.addAttribute("aladinBook", detail);
        model.addAttribute("categories", categories);
        model.addAttribute("tags", tags);

        return "/aladin/create";
    }

    @PostMapping("/admin/aladin/books")
    public String aladinCreateBook(@ModelAttribute AladinCreateRequest req) {

        bookFeignClient.createAladinBook(req);

        return "redirect:/admin/aladin/search";
    }

    // 도서 수정

    @GetMapping("admin/books/{id}/update")
    public String showUpdatePage(@PathVariable Long id, Model model) {

        BookInfoResponse book = bookFeignClient.getBookDetail(id);

        List<CategoryListResponse> categories = categoryFeignClient.getAllCategories();

        model.addAttribute("book", book);
        model.addAttribute("categories", categories);

        return "book/update";
    }

    @PostMapping(value = "/admin/books/{id}/update", consumes = "multipart/form-data")
    public String updateBook(@PathVariable Long id, @ModelAttribute BookUpdateRequest req) {

        bookFeignClient.updateBook(id, req);

        return "redirect:/admin/books/" + id;
    }

    // 도서 목록 조회, 상세페이지

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

    // 관리자 도서 목록 조회, 상세페이지


}


