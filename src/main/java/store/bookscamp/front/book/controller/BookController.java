package store.bookscamp.front.book.controller;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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
import store.bookscamp.front.book.controller.response.BookWishListResponse;
import store.bookscamp.front.booklike.controller.response.BookLikeCountResponse;
import store.bookscamp.front.booklike.controller.response.BookLikeStatusResponse;
import store.bookscamp.front.booklike.feign.BookLikeFeignClient;
import store.bookscamp.front.common.pagination.RestPageImpl;
import store.bookscamp.front.book.feign.AladinFeignClient;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.common.service.MinioService;
import store.bookscamp.front.pointhistory.controller.response.PageResponse;
import store.bookscamp.front.review.controller.response.BookReviewResponse;
import store.bookscamp.front.review.feign.ReviewFeignClient;
import store.bookscamp.front.tag.TagFeignClient;
import store.bookscamp.front.tag.controller.response.TagGetResponse;

@Slf4j
@Controller
@RequiredArgsConstructor
public class BookController {

    private static final String REDIRECT_ADMIN_BOOKS = "redirect:/admin/books";

    @Value("${app.api.prefix}")
    private String apiPrefix;

    private final MinioService minioService;
    private final AladinFeignClient aladinFeignClient;
    private final BookFeignClient bookFeignClient;
    private final TagFeignClient tagFeignClient;
    private final BookLikeFeignClient bookLikeFeignClient;
    private final ReviewFeignClient reviewFeignClient;

    @GetMapping("/admin/books")
    public String adminBooksHome(
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
                pageable.getPageSize(),
                "admin"
        );

        RestPageImpl<BookSortResponse> booksPage = response.getBody();
        model.addAttribute("booksPage", booksPage);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("keyWord", keyWord);
        model.addAttribute("sortType", sortType);

        return "admin/books";
    }

    // 수동 등록

    @GetMapping("/admin/books/new")
    public String showCreatePage(Model model) {

        List<TagGetResponse> tags = tagFeignClient.getAll(0, 1000).getContent();

        model.addAttribute("tags", tags);

        return "book/create";
    }

    @PostMapping(value = "/admin/books")
    public String createBook(
            @ModelAttribute BookCreateRequest req,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {

        if (files != null) {
            files.removeIf(MultipartFile::isEmpty); // 빈 파일 제거
        }

        List<String> imageUrls;
        if (files != null && !files.isEmpty()) {
            imageUrls = minioService.uploadFiles(files, "book");
            req.setImageUrls(imageUrls);
        }

        bookFeignClient.createBook(req, req.getPublishDate());

        return REDIRECT_ADMIN_BOOKS;
    }

    // 알라딘 등록

    @GetMapping("/admin/aladin/books")
    public String showAladinCreatePage(@RequestParam(value = "isbn",required = false) String isbn, Model model) {

        BookDetailResponse detail = aladinFeignClient.getBookDetail(isbn);
        List<TagGetResponse> tags = tagFeignClient.getAll(0, 1000).getContent();

        String originalCover = detail.getCover();
        if (originalCover != null) {
            detail.setCover(originalCover.replace("sum", "500"));
        }
        model.addAttribute("aladinBook", detail);
        model.addAttribute("tags", tags);

        return "aladin/create";
    }

    @PostMapping("/admin/aladin/books")
    public String aladinCreateBook(@ModelAttribute AladinCreateRequest req) {

        bookFeignClient.createAladinBook(req, req.getImgUrls());

        return REDIRECT_ADMIN_BOOKS;
    }

    // 도서 수정

    @GetMapping("admin/books/{id}")
    public String showUpdatePage(@PathVariable Long id, Model model) {

        BookInfoResponse book = bookFeignClient.getBookDetail(id);

        List<TagGetResponse> tags = tagFeignClient.getAll(0, 1000).getContent();

        model.addAttribute("book", book);
        model.addAttribute("tags", tags);

        return "book/update";
    }

    @PutMapping(value = "/admin/books/{id}")
    public String updateBook(
            @PathVariable Long id,
            @ModelAttribute BookUpdateRequest req,
            @RequestPart(value = "files", required = false) List<MultipartFile> files
    ) {

        if (files != null) {
            files.removeIf(MultipartFile::isEmpty); // 빈 파일 제거
        }

        if (files != null && !files.isEmpty()) {
            List<String> imageUrls = minioService.uploadFiles(files, "book");
            req.setImageUrls(imageUrls);
        }

        List<String> removedUrls = req.getRemovedUrls();
        if (removedUrls != null && !removedUrls.isEmpty()) {
            for (String url : removedUrls) {
                minioService.deleteFile(url, "book");
            }
        }

        bookFeignClient.updateBook(id, req, req.getPublishDate());

        return "redirect:/books/" + id;
    }

    // 도서 삭제
    @DeleteMapping("/admin/books")
    public String deleteBook(@RequestParam Long id) {
        bookFeignClient.deleteBook(id);
        return REDIRECT_ADMIN_BOOKS;
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
                pageable.getPageSize(),
                "user"
        );
        log.debug(response.getBody().getContent().toString());

        RestPageImpl<BookSortResponse> booksPage = response.getBody();
        model.addAttribute("booksPage", booksPage);
        model.addAttribute("categoryId", categoryId);
        model.addAttribute("keyWord", keyWord);
        model.addAttribute("sortType", sortType);

        return "book/list";
    }

    @GetMapping("/books/{id}")
    public String bookDetail(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") Long id,
            @RequestParam(defaultValue = "0") int page,
            Model model
    ){

        BookInfoResponse bookDetail = bookFeignClient.getBookDetail(id);
        model.addAttribute("book", bookDetail);

        ResponseEntity<BookLikeCountResponse> count = bookLikeFeignClient.getLikeCount(id);
        BookLikeCountResponse countResponse = count.getBody();
        model.addAttribute("bookLike", countResponse);

        boolean likeStatus = false;

        if (userDetails != null) {
            likeStatus = Optional.ofNullable(bookLikeFeignClient.getLikeStatus(id))
                    .map(ResponseEntity::getBody)
                    .map(BookLikeStatusResponse::liked)
                    .orElse(false);
        }

        model.addAttribute("isLikedByCurrentUser", likeStatus);

        model.addAttribute("apiPrefix", apiPrefix);

        String aiReview = reviewFeignClient.getAiReview(id).getBody();
        model.addAttribute("aiReview", aiReview);

        PageResponse<BookReviewResponse> reviews = reviewFeignClient.getBookReviews(id, page, 3).getBody();
        model.addAttribute("reviews", reviews);
        model.addAttribute("reviewCount", reviews != null ? reviews.getTotalElements() : 0L);
        Double avgScore = reviewFeignClient.getBookAverageScore(id).getBody();
        model.addAttribute("avgScore", avgScore);

        return "book/detail";
    }

    @GetMapping("/wishlist")
    public String wishList(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model
    ){
        if (userDetails == null){
            return "redirect:/login";
        }

        RestPageImpl<BookWishListResponse> item = bookFeignClient.getWishListBooks().getBody();

        model.addAttribute("wishlistItems", item);
        model.addAttribute("apiPrefix", apiPrefix);

        return "member/wishlist";
    }

    @GetMapping("/books/new")
    public String newBooks(
            Model model,
            @PageableDefault(size = 9, sort = "publishDate,desc") Pageable pageable
    ){
        RestPageImpl<BookSortResponse> responsePage = bookFeignClient.getNewBooks(pageable).getBody();

        model.addAttribute("responsePage", responsePage);

        return "book/latestBooks";
    }
}
