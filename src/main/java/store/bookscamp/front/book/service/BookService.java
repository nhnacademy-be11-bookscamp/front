package store.bookscamp.front.book.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import store.bookscamp.front.book.controller.response.BookIndexResponse;
import store.bookscamp.front.book.feign.BookFeignClient;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookFeignClient bookFeignClient;

    @Cacheable("index-books")
    public List<BookIndexResponse> getRecommendBooks(){
        return bookFeignClient.getRecommendBooks().getBody();
    }
}
