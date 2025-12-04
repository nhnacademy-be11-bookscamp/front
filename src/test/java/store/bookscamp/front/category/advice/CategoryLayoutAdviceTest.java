package store.bookscamp.front.category.advice;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.bookscamp.front.category.controller.response.CategoryListResponse;
import store.bookscamp.front.category.service.CategoryService;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryLayoutAdviceTest {

    @Mock
    CategoryService categoryService;

    @InjectMocks
    CategoryLayoutAdvice categoryLayoutAdvice;

    @Test
    @DisplayName("@ModelAttribute - 모든 카테고리 목록 모델 추가")
    void addCategoriesToModel_success() {
        // given
        List<CategoryListResponse> mockList = List.of(
                new CategoryListResponse(1L, "소설", List.of()),
                new CategoryListResponse(2L, "수필", List.of())
        );
        when(categoryService.getAllCategories()).thenReturn(mockList);

        // when
        List<CategoryListResponse> result = categoryLayoutAdvice.addCategoriesToModel();

        // then
        assertThat(result)
                .hasSize(2)
                .isEqualTo(mockList);
    }
}