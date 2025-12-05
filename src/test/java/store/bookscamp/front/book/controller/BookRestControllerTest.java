package store.bookscamp.front.book.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import store.bookscamp.front.book.controller.response.BookCouponResponse;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.common.pagination.RestPageImpl;

import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookRestControllerTest {

    MockMvc mvc;

    @Mock
    BookFeignClient bookFeignClient;

    @InjectMocks
    BookRestController bookRestController;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(bookRestController).build();
    }

    @Test
    @DisplayName("[POST] 위시리스트 삭제 - 성공")
    void deleteWishList_success() throws Exception {
        // given
        Long itemId = 1L;

        // when & then
        mvc.perform(post("/api-server/wishlist/{itemId}", itemId))
                .andExpect(status().isOk());

        verify(bookFeignClient).deleteWishList(itemId);
    }

    @Test
    @DisplayName("[GET] 쿠폰 적용 대상 도서 검색 - 성공")
    void getBooks_success() throws Exception {
        // given
        String keyword = "test";
        int page = 0;
        int size = 10;

        RestPageImpl<BookCouponResponse> mockPage = createRestPage(Collections.emptyList());

        when(bookFeignClient.getBooks(anyString(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(mockPage));

        // when & then
        mvc.perform(get("/api-server/admin/books/coupon")
                        .param("keyword", keyword)
                        .param("page", String.valueOf(page))
                        .param("size", String.valueOf(size)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content").isEmpty());
    }

    private <T> RestPageImpl<T> createRestPage(List<T> content) {
        return new RestPageImpl<>(
                content,
                0,    // number
                10,   // size
                (long) content.size(), // totalElements
                1,    // totalPages
                true, // last
                true, // first
                content.size(), // numberOfElements
                content.isEmpty() // empty
        );
    }
}