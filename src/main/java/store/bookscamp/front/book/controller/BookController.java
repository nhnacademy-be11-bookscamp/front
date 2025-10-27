package store.bookscamp.front.book.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.bookscamp.front.book.controller.dto.request.BookRegisterRequest;
import store.bookscamp.front.book.controller.dto.response.BookDetailResponse;
import store.bookscamp.front.book.feign.AladinApiClient;
import store.bookscamp.front.book.feign.BookApiClient;

@Controller
@RequestMapping("/book")
@RequiredArgsConstructor
public class BookController {

    private final AladinApiClient aladinApiClient;
    private final BookApiClient bookApiClient;

    @GetMapping("/register")
    public String showRegisterPage(@RequestParam(value = "isbn",required = false) String isbn, Model model) {

        BookDetailResponse detail;
        if (isbn != null && !isbn.isEmpty()) {
            detail = aladinApiClient.getBookDetail(isbn);
        } else {
            detail = new BookDetailResponse();
        }
        model.addAttribute("book", detail);
        return "books/register";
    }

    @PostMapping("/register")
    public String registerBook(@ModelAttribute BookRegisterRequest req) {
        bookApiClient.registerBook(req);
        return "books/list";
    }
  /* @PostMapping("/register")
   @ResponseBody
   public void registerBook(@RequestBody BookRegisterRequest req) {
       bookApiClient.registerBook(req);
   }*/

}


