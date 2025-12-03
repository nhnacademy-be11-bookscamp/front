package store.bookscamp.front.category.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class CategoryControllerTest {

    MockMvc mvc;

    @InjectMocks
    CategoryController categoryController;

    @BeforeEach
    void setup() {
        // @Value("${app.api.prefix}") 값 주입
        ReflectionTestUtils.setField(categoryController, "apiPrefix", "/api/test");

        mvc = MockMvcBuilders.standaloneSetup(categoryController).build();
    }

    @Test
    @DisplayName("[GET] 관리자 카테고리 페이지 조회 성공")
    void categoryPage_success() throws Exception {
        mvc.perform(get("/admin/categories"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/category"))
                .andExpect(model().attribute("apiPrefix", "/api/test"));
    }
}