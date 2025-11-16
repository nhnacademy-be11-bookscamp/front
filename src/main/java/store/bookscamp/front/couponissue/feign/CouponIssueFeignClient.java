package store.bookscamp.front.couponissue.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.common.config.FeignConfig;
import store.bookscamp.front.couponissue.controller.request.CouponIssueRequest;
import store.bookscamp.front.couponissue.controller.response.CouponIssueDownloadResponse;
import store.bookscamp.front.couponissue.controller.response.CouponIssueResponse;
import store.bookscamp.front.couponissue.controller.status.CouponFilterStatus;

@FeignClient(
        name = "coupon-issue",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface CouponIssueFeignClient {

    @GetMapping("/api-server/coupon-issue/my")
    ResponseEntity<List<CouponIssueResponse>> getMyCoupons(
            @RequestParam(name = "status", required = false, defaultValue = "ALL") CouponFilterStatus status
    );

    @DeleteMapping("/api-server/coupon-issue/{couponIssueId}")
    ResponseEntity<Void> deleteCouponIssue(@PathVariable Long couponIssueId);
}
