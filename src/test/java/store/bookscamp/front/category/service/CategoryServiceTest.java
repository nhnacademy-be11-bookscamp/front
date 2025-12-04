package store.bookscamp.front.category.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import store.bookscamp.front.category.controller.response.CategoryListResponse;
import store.bookscamp.front.category.feign.CategoryFeignClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    CategoryFeignClient categoryFeignClient;

    @InjectMocks
    CategoryService categoryService;

    @Test
    @DisplayName("모든 카테고리 목록 조회 성공")
    void getAllCategories_success() {
        // given
        // 생성자 인자: id, name, children
        List<CategoryListResponse> mockResponse = List.of(
                new CategoryListResponse(1L, "국내도서", List.of()),
                new CategoryListResponse(2L, "외국도서", List.of())
        );
        when(categoryFeignClient.getAllCategories()).thenReturn(mockResponse);

        // when
        List<CategoryListResponse> result = categoryService.getAllCategories();

        // then
        assertThat(result).hasSize(2);
        assertThat(result.getFirst().id()).isEqualTo(1L);
        assertThat(result.getFirst().name()).isEqualTo("국내도서");
        assertThat(result.getFirst().children()).isEmpty(); // 자식 카테고리 빈 리스트 확인

        verify(categoryFeignClient).getAllCategories();
    }
}