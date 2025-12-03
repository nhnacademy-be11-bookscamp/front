package store.bookscamp.front.category.controller;

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
import store.bookscamp.front.category.controller.request.CategoryCreateRequest;
import store.bookscamp.front.category.controller.request.CategoryUpdateRequest;
import store.bookscamp.front.category.feign.CategoryFeignClient;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CategoryRestControllerTest {

    MockMvc mvc;

    @Mock
    CategoryFeignClient categoryFeignClient;

    @InjectMocks
    CategoryRestController categoryRestController;

    ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(categoryRestController).build();
    }

    @Test
    @DisplayName("[POST] 카테고리 생성 요청 성공")
    void createCategory_success() throws Exception {
        // CategoryCreateRequest의 구조에 맞춰 생성자를 호출해주세요.
        // (이전 코드 예시: name만 받는 경우)
        CategoryCreateRequest request = new CategoryCreateRequest(null, "새 카테고리");

        mvc.perform(post("/api-server/admin/category/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(categoryFeignClient).createCategory(any(CategoryCreateRequest.class));
    }

    @Test
    @DisplayName("[POST] 카테고리 수정 요청 성공")
    void updateCategory_success() throws Exception {
        Long id = 1L;

        // 변경된 부분: CategoryUpdateRequest 생성자 (Category parent, String name)
        // 테스트에서는 parent에 null을 주입하여 직렬화 오류를 방지하고 흐름만 검증합니다.
        CategoryUpdateRequest request = new CategoryUpdateRequest(null, "수정된 이름");

        mvc.perform(post("/api-server/admin/category/update/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());

        verify(categoryFeignClient).updateCategory(eq(id), any(CategoryUpdateRequest.class));
    }

    @Test
    @DisplayName("[POST] 카테고리 삭제 요청 성공")
    void deleteCategory_success() throws Exception {
        Long id = 1L;

        mvc.perform(post("/api-server/admin/category/delete/{id}", id))
                .andExpect(status().isOk());

        verify(categoryFeignClient).deleteCategory(id);
    }
}