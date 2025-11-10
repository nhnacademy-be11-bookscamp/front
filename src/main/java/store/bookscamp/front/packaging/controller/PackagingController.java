package store.bookscamp.front.packaging.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import store.bookscamp.front.packaging.controller.response.PackagingGetResponse;
import store.bookscamp.front.packaging.service.PackagingService;

@Controller
@RequiredArgsConstructor
public class PackagingController {

    private final PackagingService packagingService;

    // 목록 페이지
    @GetMapping("/admin/packagings")
    public String showList(Model model) {
        List<PackagingGetResponse> packagings = packagingService.getAll();
        model.addAttribute("packagings", packagings);
        return "admin/packagings/packagings";
    }

    @GetMapping("/admin/packagings/{id}")
    public String showDetail(@PathVariable Long id, Model model) {
        PackagingGetResponse packaging = packagingService.get(id);
        model.addAttribute("packaging", packaging);
        return "admin/packagings/detail";
    }

    // 생성 폼
    @GetMapping("/admin/packagings/create")
    public String showCreatePackaging() {
        return "admin/packagings/form";
    }

    // 생성 처리 (files는 한 장만 업로드하도록 폼에서 제한하거나 서버에서 1장만 사용하도록)
    @PostMapping(value = "/admin/packagings", consumes= "multipart/form-data")
    public String createPackaging(@RequestParam String name,
                                  @RequestParam Integer price,
                                  @RequestPart(value = "files", required = false)List<MultipartFile> files) {
        packagingService.create(name, price, files);
        return "redirect:/admin/packagings";
    }


    // 수정 폼
    @GetMapping("/admin/packagings/{id}/update")
    public String showUpdate(@PathVariable Long id, Model model) {
        PackagingGetResponse packaging = packagingService.get(id);
        model.addAttribute("packaging", packaging);
        return "admin/packagings/form";
    }

    // 수정 처리 (이미지 교체는 선택)
    @PutMapping(value = "/admin/packagings/{id}/update", consumes = "multipart/form-data")
    public String updatePackaging(@PathVariable Long id,
                                  @RequestParam String name,
                                  @RequestParam Integer price,
                                  @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        packagingService.update(id, name, price, files);
        return "redirect:/admin/packagings/" + id;
    }

    @DeleteMapping("/admin/packagings/{id}/delete")
    @ResponseBody
    public ResponseEntity<Void> deletePackaging(@PathVariable Long id) {
        packagingService.delete(id);
        return ResponseEntity.noContent().build(); //"redirect:/admin/packagings";
    }
}
