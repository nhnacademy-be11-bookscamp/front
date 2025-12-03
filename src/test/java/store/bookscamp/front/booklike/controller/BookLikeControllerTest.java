package store.bookscamp.front.booklike.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import store.bookscamp.front.booklike.controller.request.BookLikeRequest;
import store.bookscamp.front.booklike.feign.BookLikeFeignClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class BookLikeRestControllerTest {

    MockMvc mvc;

    @Mock
    BookLikeFeignClient bookLikeFeignClient;

    @InjectMocks
    BookLikeRestController bookLikeRestController;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(bookLikeRestController)
                .build();
    }

    @Test
    @DisplayName("[POST] 도서 좋아요 토글 성공")
    void toggleLike_success() throws Exception {
        // given
        Long bookId = 1L;
        // 레코드 구조에 맞게 수정: boolean liked
        BookLikeRequest request = new BookLikeRequest(true);

        // when & then
        mvc.perform(post("/api-server/books/joa/{bookId}", bookId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // {"liked": true} 로 변환됨
                .andExpect(status().isOk());

        // verify
        verify(bookLikeFeignClient).toggleLike(eq(bookId), any(BookLikeRequest.class));
    }
}