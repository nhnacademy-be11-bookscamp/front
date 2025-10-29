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

    @GetMapping("/admin/aladin/search")
    public String search(@RequestParam(value = "query", required = false) String query, @RequestParam(value = "page",defaultValue = "1") int page, Model model) {
        int size = 10;
        if (query != null && !query.isBlank()) {
            AladinBookResponse response = aladinApiClient.search(query, "Title", page, 10, "Accuracy");
            model.addAttribute("books", response.getItems());
            model.addAttribute("page", page);
            model.addAttribute("totalPages", (int) Math.ceil((double) response.getTotal() / size));
            model.addAttribute("query", query);
        }
        return "aladin/search";
    }

}
