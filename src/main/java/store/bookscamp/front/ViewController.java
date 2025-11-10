package store.bookscamp.front;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import store.bookscamp.front.book.controller.response.BookIndexResponse;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.common.pagination.RestPageImpl;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final BookFeignClient bookFeignClient;

    @GetMapping("/")
    public String index(Model model) {

        ResponseEntity<List<BookIndexResponse>> response = bookFeignClient.getAllBooks();
        List<BookIndexResponse> content = response.getBody();

        model.addAttribute("books", content);

        return "index";
    }
}
