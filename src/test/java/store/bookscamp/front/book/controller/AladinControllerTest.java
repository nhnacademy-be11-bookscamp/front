package store.bookscamp.front.book.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import store.bookscamp.front.book.controller.response.AladinBookResponse;
import store.bookscamp.front.book.feign.AladinFeignClient;

import java.util.Collections;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.category.service.CategoryService;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf; // import 필요
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AladinController.class)
class AladinControllerTest {

    @Autowired
    private MockMvc mvc;

    @MockitoBean
    private AladinFeignClient aladinFeignClient;

    @MockitoBean
    private BookFeignClient bookFeignClient;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private RedisConnectionFactory redisConnectionFactory;

    @Test
    @DisplayName("검색어(query) 없이 요청 시, API 호출 없이 페이지만 반환한다")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void search_WithoutQuery_ReturnsViewOnly() throws Exception {
        // when & then
        mvc.perform(get("/admin/aladin/search")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("aladin/search"))
                .andExpect(model().attributeDoesNotExist("books"));

        verifyNoInteractions(aladinFeignClient);
    }

    @Test
    @DisplayName("검색어 입력 시, API를 호출하고 결과와 페이징 정보를 Model에 담아 반환한다")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void search_WithQuery_Success() throws Exception {
        // given
        String query = "Spring";
        int page = 1;
        int totalItems = 123;

        AladinBookResponse mockResponse = mock(AladinBookResponse.class);

        given(mockResponse.getItems()).willReturn(Collections.emptyList());
        given(mockResponse.getTotal()).willReturn(totalItems);

        given(aladinFeignClient.search(query, "Title", page, 10, "Accuracy"))
                .willReturn(mockResponse);

        // when & then
        mvc.perform(get("/admin/aladin/search")
                        .param("query", query)
                        .param("page", String.valueOf(page))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("aladin/search"))
                .andExpect(model().attributeExists("books"))
                .andExpect(model().attribute("query", query))
                .andExpect(model().attribute("page", page))
                .andExpect(model().attribute("totalPages", 13));

        verify(aladinFeignClient).search(query, "Title", page, 10, "Accuracy");
    }

    @Test
    @DisplayName("검색어가 공백일 경우 API를 호출하지 않는다")
    @WithMockUser(username = "admin", roles = "ADMIN")
    void search_WithBlankQuery_NoApiCall() throws Exception {
        // when & then
        mvc.perform(get("/admin/aladin/search")
                        .param("query", "   ")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(view().name("aladin/search"))
                .andExpect(model().attributeDoesNotExist("books"));

        verifyNoInteractions(aladinFeignClient);
    }
}