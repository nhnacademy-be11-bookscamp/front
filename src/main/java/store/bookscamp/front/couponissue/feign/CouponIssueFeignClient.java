package store.bookscamp.front.couponissue.feign;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import store.bookscamp.front.common.config.FeignConfig;
import store.bookscamp.front.couponissue.controller.request.CouponIssueRequest;
import store.bookscamp.front.couponissue.controller.response.CouponIssueResponse;

@FeignClient(
        name = "coupon-issue",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface CouponIssueFeignClient {

    @PostMapping("/api-server/coupon-issue/issue")
    ResponseEntity<Long> issueCoupon(@Valid @RequestBody CouponIssueRequest couponIssueRequest);

    @GetMapping("/api-server/coupon-issue/my")
    ResponseEntity<List<CouponIssueResponse>> getMyCoupons();

    @DeleteMapping("/api-server/coupon-issue/{couponIssueId}")
    ResponseEntity<Void> deleteCouponIssue(@PathVariable Long couponIssueId);
}
