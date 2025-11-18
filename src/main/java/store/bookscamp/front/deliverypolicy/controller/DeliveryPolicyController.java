package store.bookscamp.front.deliverypolicy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import store.bookscamp.front.deliverypolicy.controller.request.DeliveryPolicyCreateRequest;
import store.bookscamp.front.deliverypolicy.controller.request.DeliveryPolicyUpdateRequest;
import store.bookscamp.front.deliverypolicy.controller.response.DeliveryPolicyResponse;
import store.bookscamp.front.deliverypolicy.service.DeliveryPolicyService;

@Controller
@RequiredArgsConstructor
public class DeliveryPolicyController {

    private final DeliveryPolicyService service;

    /*
    @GetMapping("/admin/delivery-policy")
    public String getAdminDeliveryPolicy(Model model) {
        model.addAttribute("policy", service.getDeliveryPolicy());
        return "admin/delivery-policy";
    }

    @PostMapping("/admin/delivery-policy") // PutMapping
    public String updateDeliveryPolicy(@Valid DeliveryPolicyUpdateRequest request) {
        service.updateDeliveryPolicy(request);
        return "redirect:/admin/delivery-policy";
    }
     */

    @GetMapping("/admin/delivery-policy")
    public String getDeliveryPolicy(Model model) {
        try {
            DeliveryPolicyResponse policy = service.getDeliveryPolicy();
            DeliveryPolicyUpdateRequest form = new DeliveryPolicyUpdateRequest(
                    policy.getFreeDeliveryThreshold(),
                    policy.getBaseDeliveryFee()
            );
            model.addAttribute("policy", policy);
            model.addAttribute("form", form);
            model.addAttribute("isNew", false);
        } catch (Exception ex) {
            model.addAttribute("form", new DeliveryPolicyCreateRequest());
            model.addAttribute("isNew", true);
        }

        return "admin/delivery-policy";
    }

    @PostMapping("/admin/delivery-policy")
    public String submitDeliveryPolicy(
            @Valid @ModelAttribute("form") DeliveryPolicyUpdateRequest form,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes ra) {

        if (bindingResult.hasErrors()) {
            try {
                DeliveryPolicyResponse policy = service.getDeliveryPolicy();
                model.addAttribute("policy", policy);
                model.addAttribute("isNew", false);
            } catch (Exception ex) {
                model.addAttribute("isNew", true);
            }
            return "admin/delivery-policy";
        }

        try {
            // 정책이 이미 있으면 → 수정
            service.updateDeliveryPolicy(form);
            ra.addFlashAttribute("successMessage", "수정되었습니다.");
        } catch (Exception ex) {
            // 정책이 없어서 update 실패 → 생성 시도
            service.createDeliveryPolicy(
                    new DeliveryPolicyCreateRequest(
                            form.freeDeliveryThreshold(),
                            form.baseDeliveryFee()
                    )
            );
            ra.addFlashAttribute("successMessage", "등록되었습니다.");
        }
        return "redirect:/admin/delivery-policy";
    }

//    @PostMapping("/admin/delivery-policy")
//    public String updateDeliveryPolicy(
//            @Valid @ModelAttribute("form") DeliveryPolicyUpdateRequest form,
//            BindingResult bindingResult,
//            Model model,
//            RedirectAttributes ra) {
//        if (bindingResult.hasErrors()) {
//            model.addAttribute("policy", service.getDeliveryPolicy());
//            return "admin/delivery-policy";
//        }
//        service.updateDeliveryPolicy(form);
//        ra.addFlashAttribute("successMessage", "저장되었습니다.");
//        return "redirect:/admin/delivery-policy";
//    }
}