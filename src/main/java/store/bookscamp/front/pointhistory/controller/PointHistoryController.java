package store.bookscamp.front.pointhistory.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.pointhistory.controller.response.PageResponse;
import store.bookscamp.front.pointhistory.controller.response.PointHistoryResponse;
import store.bookscamp.front.pointhistory.feign.PointHistoryFeignClient;

@Controller
@RequiredArgsConstructor
public class PointHistoryController {

    private final PointHistoryFeignClient pointHistoryFeignClient;

    @GetMapping("/mypage/points")
    public String getMyPoints(@RequestParam(defaultValue = "0") int page, Model model) {

        PageResponse<PointHistoryResponse> response = pointHistoryFeignClient.getMyPointHistories(page, 10).getBody();

        model.addAttribute("points", response.getContent());
        model.addAttribute("page", response);

        return "pointHistory/mypoint";
    }


}
