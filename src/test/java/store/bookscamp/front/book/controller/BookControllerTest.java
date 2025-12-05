package store.bookscamp.front.book.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

// DTO Imports
import store.bookscamp.front.book.controller.request.AladinCreateRequest;
import store.bookscamp.front.book.controller.request.BookCreateRequest;
import store.bookscamp.front.book.controller.request.BookUpdateRequest;
import store.bookscamp.front.book.controller.response.BookDetailResponse;
import store.bookscamp.front.book.controller.response.BookIndexResponse; // [추가]
import store.bookscamp.front.book.controller.response.BookInfoResponse;
import store.bookscamp.front.book.controller.response.BookSortResponse;
import store.bookscamp.front.book.controller.response.BookWishListResponse;
import store.bookscamp.front.book.status.BookStatus;
import store.bookscamp.front.booklike.controller.response.BookLikeCountResponse;
import store.bookscamp.front.booklike.controller.response.BookLikeStatusResponse;
import store.bookscamp.front.pointhistory.controller.response.PageResponse;
import store.bookscamp.front.review.controller.response.BookReviewResponse;
import store.bookscamp.front.tag.controller.response.TagGetResponse;
import store.bookscamp.front.common.pagination.RestPageImpl;

// Feign Clients & Services
import store.bookscamp.front.book.feign.AladinFeignClient;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.booklike.feign.BookLikeFeignClient;
import store.bookscamp.front.common.service.MinioService;
import store.bookscamp.front.review.feign.ReviewFeignClient;
import store.bookscamp.front.tag.TagFeignClient;

import java.security.Principal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class BookControllerTest {

    MockMvc mvc;

    @Mock
    MinioService minioService;
    @Mock
    AladinFeignClient aladinFeignClient;
    @Mock
    BookFeignClient bookFeignClient;
    @Mock
    TagFeignClient tagFeignClient;
    @Mock
    BookLikeFeignClient bookLikeFeignClient;
    @Mock
    ReviewFeignClient reviewFeignClient;

    @InjectMocks
    BookController bookController;

    @BeforeEach
    void setup() {
        ReflectionTestUtils.setField(bookController, "apiPrefix", "/api/v1");

        mvc = MockMvcBuilders.standaloneSetup(bookController)
                .setCustomArgumentResolvers(
                        new PageableHandlerMethodArgumentResolver(),
                        new HandlerMethodArgumentResolver() {
                            @Override
                            public boolean supportsParameter(org.springframework.core.MethodParameter parameter) {
                                return UserDetails.class.isAssignableFrom(parameter.getParameterType());
                            }

                            @Override
                            public Object resolveArgument(org.springframework.core.MethodParameter parameter,
                                                          ModelAndViewContainer mavContainer,
                                                          NativeWebRequest webRequest,
                                                          org.springframework.web.bind.support.WebDataBinderFactory binderFactory) {
                                Principal principal = webRequest.getUserPrincipal();
                                if (principal instanceof Authentication) {
                                    return ((Authentication) principal).getPrincipal();
                                }
                                return null;
                            }
                        }
                )
                .build();
    }

    @Test
    @DisplayName("[GET] 관리자 도서 목록 조회 - 성공")
    void adminBooksHome_success() throws Exception {
        RestPageImpl<BookSortResponse> mockPage = createRestResponse(Collections.emptyList());

        when(bookFeignClient.getBooks(any(), any(), anyString(), anyInt(), anyInt(), anyString()))
                .thenReturn(ResponseEntity.ok(mockPage));

        mvc.perform(get("/admin/books")
                        .param("page", "0")
                        .param("size", "10")
                        .param("sortType", "id"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/books"))
                .andExpect(model().attributeExists("booksPage"));
    }

    @Test
    @DisplayName("[GET] 도서 등록 페이지 조회 (태그 포함) - 성공")
    void showCreatePage_success() throws Exception {
        List<TagGetResponse> tagList = List.of(new TagGetResponse(1L, "IT"));
        Page<TagGetResponse> mockTags = createTagPage(tagList);

        when(tagFeignClient.getAll(anyInt(), anyInt())).thenReturn(mockTags);

        mvc.perform(get("/admin/books/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/create"))
                .andExpect(model().attribute("tags", tagList));
    }

    @Test
    @DisplayName("[POST] 도서 등록 처리 (파일 업로드 포함) - 성공")
    void createBook_success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("files", "test.jpg",
                MediaType.IMAGE_JPEG_VALUE, "content".getBytes());

        List<String> imageUrls = List.of("http://minio/test.jpg");

        when(minioService.uploadFiles(anyList(), eq("book")))
                .thenReturn(imageUrls);

        mvc.perform(multipart("/admin/books")
                        .file(file)
                        .param("title", "Test Book")
                        .param("publishDate", LocalDate.now().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/books"));

        verify(bookFeignClient).createBook(any(BookCreateRequest.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("[GET] 알라딘 도서 등록 페이지 조회 - 성공")
    void showAladinCreatePage_success() throws Exception {
        String isbn = "9781234567890";

        BookDetailResponse mockDetail = new BookDetailResponse();
        mockDetail.setCover("http://aladin/sum/cover.jpg");

        Page<TagGetResponse> mockTags = createTagPage(Collections.emptyList());

        when(aladinFeignClient.getBookDetail(isbn)).thenReturn(mockDetail);
        when(tagFeignClient.getAll(anyInt(), anyInt())).thenReturn(mockTags);

        mvc.perform(get("/admin/aladin/books").param("isbn", isbn))
                .andExpect(status().isOk())
                .andExpect(view().name("aladin/create"))
                .andExpect(model().attributeExists("aladinBook"));
    }

    @Test
    @DisplayName("[POST] 알라딘 도서 등록 처리 - 성공")
    void aladinCreateBook_success() throws Exception {
        mvc.perform(post("/admin/aladin/books")
                        .param("title", "Aladin Book")
                        .param("imgUrls", "http://image.url/1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/books"));

        verify(bookFeignClient).createAladinBook(any(AladinCreateRequest.class), anyList());
    }

    @Test
    @DisplayName("[GET] 도서 수정 페이지 조회 - 성공")
    void showUpdatePage_success() throws Exception {
        Long bookId = 1L;

        BookInfoResponse mockBook = createMockBookInfoResponse(bookId);

        Page<TagGetResponse> mockTags =
                new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 1000), 0);

        when(bookFeignClient.getBookDetail(bookId)).thenReturn(mockBook);
        when(tagFeignClient.getAll(anyInt(), anyInt())).thenReturn(mockTags);

        mvc.perform(get("/admin/books/{id}", bookId))
                .andExpect(status().isOk())
                .andExpect(view().name("book/update"))
                .andExpect(model().attributeExists("book"))
                .andExpect(model().attributeExists("tags"));
    }

    @Test
    @DisplayName("[PUT] 도서 수정 처리 - 성공")
    void updateBook_success() throws Exception {
        Long bookId = 1L;

        MockMultipartFile file = new MockMultipartFile("files", "new.jpg",
                MediaType.IMAGE_JPEG_VALUE, "content".getBytes());

        mvc.perform(multipart("/admin/books/{id}", bookId)
                        .file(file)
                        .param("removedUrls", "old-url")
                        .param("publishDate", LocalDate.now().toString())
                        .with(req -> {
                            req.setMethod("PUT");
                            return req;
                        }))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/books/" + bookId));

        verify(minioService).deleteFile("old-url", "book");
        verify(bookFeignClient).updateBook(eq(bookId),
                any(BookUpdateRequest.class), any(LocalDate.class));
    }

    @Test
    @DisplayName("[DELETE] 도서 삭제 처리 - 성공")
    void deleteBook_success() throws Exception {
        Long bookId = 1L;

        mvc.perform(delete("/admin/books")
                        .param("id", String.valueOf(bookId)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/books"));

        verify(bookFeignClient).deleteBook(bookId);
    }

    @Test
    @DisplayName("[GET] 사용자 도서 목록 조회 - 성공")
    void listBook_success() throws Exception {
        RestPageImpl<BookSortResponse> mockPage = createRestResponse(Collections.emptyList());

        when(bookFeignClient.getBooks(any(), any(), anyString(), anyInt(), anyInt(), eq("user")))
                .thenReturn(ResponseEntity.ok(mockPage));

        mvc.perform(get("/books"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/list"))
                .andExpect(model().attributeExists("booksPage"));
    }

    @Test
    @DisplayName("[GET] 도서 상세 페이지 (로그인 사용자) - 성공")
    void bookDetail_user_success() throws Exception {
        Long bookId = 1L;

        BookInfoResponse mockBook = createMockBookInfoResponse(bookId);
        BookLikeCountResponse mockLikeCount = new BookLikeCountResponse(10L);
        BookLikeStatusResponse mockLikeStatus = new BookLikeStatusResponse(true);

        PageResponse<BookReviewResponse> mockReviews =
                new PageResponse<>(Collections.emptyList(), 0, 1, 0L, true, true);

        UserDetails mockUser = new User("user", "password", Collections.emptyList());

        when(bookFeignClient.getBookDetail(bookId)).thenReturn(mockBook);
        when(bookLikeFeignClient.getLikeCount(bookId)).thenReturn(ResponseEntity.ok(mockLikeCount));
        when(bookLikeFeignClient.getLikeStatus(bookId)).thenReturn(ResponseEntity.ok(mockLikeStatus));
        when(reviewFeignClient.getAiReview(bookId)).thenReturn(ResponseEntity.ok("AI Summary"));
        when(reviewFeignClient.getBookReviews(eq(bookId), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(mockReviews));
        when(reviewFeignClient.getBookAverageScore(bookId))
                .thenReturn(ResponseEntity.ok(4.5));

        mvc.perform(get("/books/{id}", bookId)
                        .principal(new UsernamePasswordAuthenticationToken(mockUser, null)))
                .andExpect(status().isOk())
                .andExpect(view().name("book/detail"))
                .andExpect(model().attribute("isLikedByCurrentUser", true));
    }

    @Test
    @DisplayName("[GET] 신간 도서 목록 조회 - 성공")
    void newBooks_success() throws Exception {
        RestPageImpl<BookSortResponse> mockPage = createRestResponse(Collections.emptyList());

        when(bookFeignClient.getNewBooks(any(Pageable.class)))
                .thenReturn(ResponseEntity.ok(mockPage));

        mvc.perform(get("/books/new"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/latestBooks"))
                .andExpect(model().attribute("responsePage", mockPage));
    }

    // [추가됨] 베스트 셀러 페이지 조회 테스트
    @Test
    @DisplayName("[GET] 베스트 셀러 페이지 조회 - 성공")
    void bestSeller_success() throws Exception {
        // given
        // BookIndexResponse는 실제 DTO를 사용하거나 빈 리스트여도 됨
        RestPageImpl<BookIndexResponse> mockPage = createRestResponse(Collections.emptyList());

        when(bookFeignClient.getBestSellers(any(Pageable.class)))
                .thenReturn(ResponseEntity.ok(mockPage));

        // when & then
        mvc.perform(get("/books/best"))
                .andExpect(status().isOk())
                .andExpect(view().name("book/bestseller"))
                .andExpect(model().attributeExists("responsePage"))
                .andExpect(model().attribute("responsePage", mockPage));
    }

    @Test
    @DisplayName("[GET] 위시리스트 조회 - 성공")
    void wishList_success() throws Exception {
        RestPageImpl<BookWishListResponse> mockList = createRestResponse(Collections.emptyList());

        UserDetails mockUser = new User("user", "password", Collections.emptyList());

        when(bookFeignClient.getWishListBooks()).thenReturn(ResponseEntity.ok(mockList));

        mvc.perform(get("/wishlist")
                        .principal(new UsernamePasswordAuthenticationToken(mockUser, null)))
                .andExpect(status().isOk())
                .andExpect(view().name("member/wishlist"));
    }

    @Test
    @DisplayName("[GET] 위시리스트 조회 (비로그인) - 리다이렉트")
    void wishList_unauthorized() throws Exception {
        mvc.perform(get("/wishlist"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));
    }


    private BookInfoResponse createMockBookInfoResponse(Long id) {
        return new BookInfoResponse(
                id,
                "Test Title",
                "Explanation",
                "Content",
                "Publisher",
                LocalDate.now(),
                "Contributors",
                "1234567890",
                BookStatus.AVAILABLE,
                true,
                20000,
                18000,
                100,
                500L,
                Collections.emptyList(),
                Collections.emptyList(),
                Collections.emptyList()
        );
    }

    private Page<TagGetResponse> createTagPage(List<TagGetResponse> list) {
        return new PageImpl<>(list, PageRequest.of(0, 1000), list.size());
    }

    private <T> RestPageImpl<T> createRestResponse(List<T> content) {
        return new RestPageImpl<>(
                content,
                0,
                10,
                (long) content.size(),
                1,
                true,
                true,
                content.size(),
                content.isEmpty()
        );
    }
}