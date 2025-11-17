package store.bookscamp.front.pointpolicy.controller;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import store.bookscamp.front.pointpolicy.controller.enums.PointPolicyType;
import store.bookscamp.front.pointpolicy.controller.enums.RewardType;
import store.bookscamp.front.pointpolicy.controller.request.PointPolicyCreateRequest;
import store.bookscamp.front.pointpolicy.controller.request.PointPolicyUpdateRequest;
import store.bookscamp.front.pointpolicy.controller.response.PointPolicyResponse;
import store.bookscamp.front.pointpolicy.feign.PointPolicyFeignClient;

@Controller
@RequestMapping("/admin/point-policies")
@RequiredArgsConstructor
public class PointPolicyController {

    private final PointPolicyFeignClient pointPolicyFeignClient;

    @PostMapping
    public ResponseEntity<Void> createPointPolicy(
            @RequestBody @Valid PointPolicyCreateRequest request
    ) {
        ResponseEntity<Void> response = pointPolicyFeignClient.createPointPolicy(request);
        return ResponseEntity.status(response.getStatusCode()).build();
    }

    @PutMapping("/{pointPolicyId}")
    public ResponseEntity<Void> updatePointPolicy(
            @PathVariable Long pointPolicyId,
            @RequestBody @Valid PointPolicyUpdateRequest request
    ) {
        ResponseEntity<Void> response = pointPolicyFeignClient.updatePointPolicy(pointPolicyId, request);
        return ResponseEntity.status(response.getStatusCode()).build();
    }

    @DeleteMapping("/{pointPolicyId}")
    public ResponseEntity<Void> deletePointPolicy(
            @PathVariable Long pointPolicyId
    ) {
        ResponseEntity<Void> response = pointPolicyFeignClient.deletePointPolicy(pointPolicyId);
        return ResponseEntity.status(response.getStatusCode()).build();
    }

    @GetMapping
    public String listPointPolicies(Model model) {
        ResponseEntity<List<PointPolicyResponse>> response = pointPolicyFeignClient.listPointPolicies();
        List<PointPolicyResponse> pointPolicies = response.getBody();

        model.addAttribute("pointPolicies", pointPolicies);
        model.addAttribute("pointPolicyTypes", PointPolicyType.values());
        model.addAttribute("rewardTypes", RewardType.values());

        return "admin/point-policy";
    }
}
