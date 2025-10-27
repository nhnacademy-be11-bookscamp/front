package store.bookscamp.front.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.book.controller.dto.response.AladinBookResponse;
import store.bookscamp.front.book.feign.AladinApiClient;

@Controller
@RequiredArgsConstructor
public class AladinController {
    private final AladinApiClient aladinApiClient;

    /*@GetMapping("/aladin/search")
    public String testAladin(@RequestParam String q) {
        return aladinApiClient.search(q, "Title", 1, 5, "Accuracy");
    }*/

    @GetMapping("/aladin/search")
    public String search(@RequestParam(value = "query", required = false) String query, Model model) {
        if (query != null && !query.isBlank()) {
            AladinBookResponse response = aladinApiClient.search(query, "Title", 1, 10, "Accuracy");
            model.addAttribute("books", response.getItems());
        }
        return "aladin/search";
    }

}
