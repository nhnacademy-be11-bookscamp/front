package store.bookscamp.front.tag.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import store.bookscamp.front.tag.TagFeignClient;
import store.bookscamp.front.tag.controller.request.TagCreateRequest;
import store.bookscamp.front.tag.controller.request.TagUpdateRequest;
import feign.FeignException;
import store.bookscamp.front.tag.controller.response.TagGetResponse;

@Controller
@RequiredArgsConstructor
@RequestMapping("/admin")
public class TagController {

    private final TagFeignClient tagFeignClient;

    @Value("${gateway.base-url}")
    private String pathPrefix;

    // 태그 전체 목록 보여줌 : Ajax를 위해 작성됨 버전2
    @GetMapping("/tags")
    public ModelAndView showTagPage() {
        List<TagGetResponse> tags = tagFeignClient.getAll();

        ModelAndView mv = new ModelAndView("/tags/tag");
        mv.addObject("tags", tags);
        mv.addObject("apiPrefix", pathPrefix);
        return mv;
    }


    // 태그 전체 목록 보여줌 : 버전1
    /*
    @GetMapping("/tags")
    public ModelAndView showTagPage() {
        List<TagGetResponse> tags = tagFeignClient.getAll();

        ModelAndView modelAndView = new ModelAndView("tags/tag");
        modelAndView.addObject("tags", tags);
        return modelAndView;
    }

     */

    // 태그 생성
    @PostMapping("/tags")
    public String create(@RequestParam("name") String name, Model model) {
        try {
            tagFeignClient.createTag(new TagCreateRequest(name));
        } catch (FeignException e) {
            model.addAttribute("error", "이미 존재하는 태그입니다.");
            model.addAttribute("tags", tagFeignClient.getAll()); // ← 리스트 다시 채우기
            return "tags/tag";
        }
        return "redirect:/admin/tags";
    }

    @PostMapping("/tags/{id}")
    public String updateTag(@PathVariable Long id,
                            @RequestParam("name") String name) {
        tagFeignClient.updateTag(id, new TagUpdateRequest(name));
        return "redirect:/admin/tags";
    }

    @PostMapping("/tags/{id}/delete")
    public String deleteTag(@PathVariable Long id) {
        tagFeignClient.deleteTag(id);
        return "redirect:/admin/tags";
    }
}