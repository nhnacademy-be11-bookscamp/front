package store.bookscamp.front.deliverypolicy.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.deliverypolicy.controller.request.DeliveryPolicyCreateRequest;
import store.bookscamp.front.deliverypolicy.controller.response.DeliveryPolicyResponse;
import store.bookscamp.front.deliverypolicy.service.DeliveryPolicyService;

@Controller
@RequiredArgsConstructor
public class DeliveryPolicyController {

    private final DeliveryPolicyService deliveryPolicyService;

    @GetMapping("/admin/delivery-policies")
    public String viewAdminDeliveryPolicies(Model model) {
        model.addAttribute("policies", deliveryPolicyService.getAllDeliveryPolicies());
        return "admin/delivery-policy";
    }

    @PostMapping("/admin/delivery-policies")
    public String createDeliveryPolicy(DeliveryPolicyCreateRequest createRequest) {
        deliveryPolicyService.createDeliveryPolicy(createRequest);
        return "redirect:/admin/delivery-policies"; // 단수인지 복수인지
    }

    @GetMapping("/admin/delivery-polices/is-free")
    public String isFreeByTotal(@RequestParam int orderTotal, Model model) {
        boolean isFree = deliveryPolicyService.isFreeByTotal(orderTotal);
        model.addAttribute("isFree", isFree);
        return "/admin/delivery-policy";
    }
}
