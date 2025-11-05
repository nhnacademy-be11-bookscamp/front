package store.bookscamp.front.book.controller;


import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.bookscamp.front.book.controller.request.BookCreateRequest;
import store.bookscamp.front.book.controller.response.BookDetailResponse;
import store.bookscamp.front.book.controller.response.BookInfoResponse;
import store.bookscamp.front.book.controller.response.BookSortResponse;
import store.bookscamp.front.booklike.controller.response.BookLikeCountResponse;
import store.bookscamp.front.booklike.controller.response.BookLikeStatusResponse;
import store.bookscamp.front.booklike.feign.BookLikeFeginClient;
import store.bookscamp.front.category.service.CategoryService;
import store.bookscamp.front.common.pagination.RestPageImpl;
import store.bookscamp.front.book.feign.AladinFeignClient;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.category.controller.response.CategoryListResponse;

@Controller
@RequiredArgsConstructor
public class BookController {

    @Value("${gateway.base-url}")
    private String pathPrefix;

    private final AladinFeignClient aladinFeignClient;
    private final BookFeignClient bookFeignClient;
    private final BookLikeFeginClient bookLikeFeginClient;
    private final CategoryService categoryService;

    @GetMapping("/admin/books")
    public String adminBooksHome() {
        return "admin/books";
    }

    @GetMapping("/admin/books/new")
    public String showCreatePage(@RequestParam(value = "isbn",required = false) String isbn, Model model) {

        BookDetailResponse detail;
        if (isbn != null && !isbn.isEmpty()) {
            detail = aladinFeignClient.getBookDetail(isbn);
        } else {
            detail = new BookDetailResponse();
        }
        model.addAttribute("book", detail);
        return "book/create";
    }

    @PostMapping("/admin/books")
    public String createBook(@ModelAttribute BookCreateRequest req) {
        bookFeignClient.createBook(req);
        return "redirect:/admin/books";
    }
  /* @PostMapping("/register")
   @ResponseBody
   public void registerBook(@RequestBody BookRegisterRequest req) {
       bookApiClient.registerBook(req);
   }*/

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

        List<CategoryListResponse> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        model.addAttribute("categoryId", categoryId);
        model.addAttribute("keyWord", keyWord);
        model.addAttribute("sortType", sortType);

        return "book/list";
    }

    @GetMapping({"/books/{id}","/admin/books/{id}"})
    public String bookDetail(
            @PathVariable("id") Long id,
            HttpServletRequest request,
            Model model
    ){

        BookInfoResponse bookDetail = bookFeignClient.getBookDetail(id);
        model.addAttribute("book", bookDetail);

        ResponseEntity<BookLikeCountResponse> count = bookLikeFeginClient.getLikeCount(id);
        BookLikeCountResponse countResponse = count.getBody();
        model.addAttribute("bookLike", countResponse);

        ResponseEntity<BookLikeStatusResponse> likeStatus = bookLikeFeginClient.getLikeStatus(id);
        boolean likedByCurrentUser = Optional.ofNullable(likeStatus.getBody())
                .map(BookLikeStatusResponse::liked)
                .orElse(false);
        model.addAttribute("isLikedByCurrentUser", likedByCurrentUser);

        model.addAttribute("apiPrefix", pathPrefix);

        String uri = request.getRequestURI();

        if (uri.startsWith("/admin")) {
            return "admin/books";
        } else {
            return "book/detail";
        }
    }
}


