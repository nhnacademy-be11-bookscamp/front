package store.bookscamp.front.book;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class BookViewController {

    @GetMapping
    public String listBook(){

        return "books/list"
;    }
}
