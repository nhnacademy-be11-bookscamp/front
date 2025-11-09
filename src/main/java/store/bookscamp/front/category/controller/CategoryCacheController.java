package store.bookscamp.front.category.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/cache")
public class CategoryCacheController {

    private final CacheManager cacheManager;

    @PostMapping("/evict-categories")
    public ResponseEntity<Void> evictCategoriesCache() {
        Cache cache = cacheManager.getCache("categories");
        if (cache != null) {
            cache.clear();
        }
        return ResponseEntity.ok().build();
    }
}