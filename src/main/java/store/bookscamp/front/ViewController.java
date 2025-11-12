package store.bookscamp.front;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import store.bookscamp.front.book.controller.response.BookIndexResponse;
import store.bookscamp.front.book.service.BookService;

@Controller
@RequiredArgsConstructor
public class ViewController {

    private final BookService bookService;

    @GetMapping("/")
    public String index(Model model) {

        List<BookIndexResponse> content = bookService.getRecommendBooks();

        model.addAttribute("books", content);

        return "index";
    }
}
