package store.bookscamp.front.book.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.book.controller.response.AladinBookResponse;
import store.bookscamp.front.book.feign.AladinFeignClient;

@Controller
@RequiredArgsConstructor
public class AladinController {
    private final AladinFeignClient aladinFeignClient;

    @GetMapping("/admin/aladin/search")
    public String search(
            @RequestParam(value = "query", required = false) String query,
            @RequestParam(value = "page",defaultValue = "1") int page,
            Model model
    ) {
        int size = 10;
        if (query != null && !query.isBlank()) {
            AladinBookResponse response = aladinFeignClient.search(query, "Title", page, 10, "Accuracy");
            model.addAttribute("books", response.getItems());
            model.addAttribute("page", page);
            model.addAttribute("totalPages", (int) Math.ceil((double) response.getTotal() / size));
            model.addAttribute("query", query);
        }
        return "aladin/search";
    }

}
