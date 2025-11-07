package store.bookscamp.front.category.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import store.bookscamp.front.category.controller.response.CategoryListResponse;
import store.bookscamp.front.category.feign.CategoryFeignClient;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryFeignClient categoryFeignClient;

    // Feign Client 호출 결과를 "categories"라는 이름으로 캐싱합니다.
    @Cacheable("categories")
    public List<CategoryListResponse> getAllCategories() {
        // 캐시에 데이터가 없으면 이 부분이 실행됩니다 (API 호출)
        // 캐시에 데이터가 있으면 이 부분은 실행되지 않고 바로 캐시 값을 반환합니다.
        return categoryFeignClient.getAllCategories();
    }
}