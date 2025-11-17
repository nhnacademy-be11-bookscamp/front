package store.bookscamp.front.pointpolicy.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import store.bookscamp.front.common.config.FeignConfig;
import store.bookscamp.front.pointpolicy.controller.request.PointPolicyCreateRequest;
import store.bookscamp.front.pointpolicy.controller.request.PointPolicyUpdateRequest;
import store.bookscamp.front.pointpolicy.controller.response.PointPolicyResponse;

@FeignClient(
        name = "api-through-gateway-pointpolicy",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface PointPolicyFeignClient {

    @PostMapping("/api-server/admin/point-policies")
    ResponseEntity<Void> createPointPolicy(PointPolicyCreateRequest request);

    @PutMapping("/api-server/admin/point-policies/{pointPolicyId}")
    ResponseEntity<Void> updatePointPolicy(@PathVariable Long pointPolicyId, PointPolicyUpdateRequest request);

    @DeleteMapping("/api-server/admin/point-policies/{pointPolicyId}")
    ResponseEntity<Void> deletePointPolicy(@PathVariable Long pointPolicyId);

    @GetMapping("/api-server/admin/point-policies")
    ResponseEntity<List<PointPolicyResponse>> listPointPolicies();

    @GetMapping("/api-server/admin/point-policies/{pointPolicyId}")
    ResponseEntity<PointPolicyResponse> getPointPolicy(@PathVariable Long pointPolicyId);
}
