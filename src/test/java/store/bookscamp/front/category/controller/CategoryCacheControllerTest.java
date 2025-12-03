package store.bookscamp.front.category.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class CategoryCacheControllerTest {

    MockMvc mvc;

    @Mock
    CacheManager cacheManager;

    @Mock
    Cache cache;

    @InjectMocks
    CategoryCacheController categoryCacheController;

    @BeforeEach
    void setup() {
        mvc = MockMvcBuilders.standaloneSetup(categoryCacheController).build();
    }

    @Test
    @DisplayName("[POST] 카테고리 캐시 삭제 성공")
    void evictCategoriesCache_success() throws Exception {
        // given
        when(cacheManager.getCache("categories")).thenReturn(cache);

        // when
        mvc.perform(post("/internal/cache/evict-categories"))
                .andExpect(status().isOk());

        // then
        verify(cacheManager).getCache("categories");
        verify(cache).clear(); // clear()가 호출되었는지 확인
    }

    @Test
    @DisplayName("[POST] 캐시가 존재하지 않아도 에러 없이 성공")
    void evictCategoriesCache_null_safe() throws Exception {
        // given
        when(cacheManager.getCache("categories")).thenReturn(null);

        // when
        mvc.perform(post("/internal/cache/evict-categories"))
                .andExpect(status().isOk());

        // then
        verify(cacheManager).getCache("categories");
        verify(cache, never()).clear(); // null이면 clear 호출 안 함
    }
}