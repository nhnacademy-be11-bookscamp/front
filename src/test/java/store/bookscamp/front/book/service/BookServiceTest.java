package store.bookscamp.front.book.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import store.bookscamp.front.book.controller.response.BookIndexResponse;
import store.bookscamp.front.book.feign.BookFeignClient;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

    @Mock
    BookFeignClient bookFeignClient;

    @InjectMocks
    BookService bookService;

    @Test
    @DisplayName("추천 도서 목록 조회 - 성공")
    void getRecommendBooks_success() {
        // given
        List<BookIndexResponse> mockResponseList = Collections.emptyList();

        when(bookFeignClient.getRecommendBooks())
                .thenReturn(ResponseEntity.ok(mockResponseList));

        // when
        List<BookIndexResponse> result = bookService.getRecommendBooks();

        // then
        assertThat(result).isEqualTo(mockResponseList);

        verify(bookFeignClient, times(1)).getRecommendBooks();
    }
}