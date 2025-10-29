package store.bookscamp.front.book.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import store.bookscamp.front.book.controller.dto.request.BookCreateRequest;
import store.bookscamp.front.book.controller.dto.response.BookDetailResponse;
import store.bookscamp.front.book.feign.AladinApiClient;
import store.bookscamp.front.book.feign.BookApiClient;

@Controller
@RequestMapping
@RequiredArgsConstructor
public class BookController {

    private final AladinApiClient aladinApiClient;
    private final BookApiClient bookApiClient;

    @GetMapping("/admin/books/create")
    public String showCreatePage(@RequestParam(value = "isbn",required = false) String isbn, Model model) {

        BookDetailResponse detail;
        if (isbn != null && !isbn.isEmpty()) {
            detail = aladinApiClient.getBookDetail(isbn);
        } else {
            detail = new BookDetailResponse();
        }
        model.addAttribute("book", detail);
        return "book/create";
    }

    @PostMapping("/admin/books/create")
    public String createBook(@ModelAttribute BookCreateRequest req) {
        bookApiClient.createBook(req);
        return "redirect:/books";
    }
  /* @PostMapping("/register")
   @ResponseBody
   public void registerBook(@RequestBody BookRegisterRequest req) {
       bookApiClient.registerBook(req);
   }*/

}


