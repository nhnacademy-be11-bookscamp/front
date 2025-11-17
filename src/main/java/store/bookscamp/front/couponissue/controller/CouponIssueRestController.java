package store.bookscamp.front.couponissue.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import store.bookscamp.front.couponissue.controller.request.CouponIssueRequest;
import store.bookscamp.front.couponissue.controller.response.CouponIssueDownloadResponse;
import store.bookscamp.front.couponissue.feign.CouponIssueFeignClient;

@RestController
@RequiredArgsConstructor
public class CouponIssueRestController {

    private final CouponIssueFeignClient couponIssueFeignClient;

    @DeleteMapping("/api-server/coupon-issue/{couponIssueId}")
    public void deleteCouponIssue(@PathVariable Long couponIssueId){
        couponIssueFeignClient.deleteCouponIssue(couponIssueId);
    }

    @GetMapping("/api-server/coupon-issue/downloadable/{bookId}")
    public List<CouponIssueDownloadResponse> getDownloadableCoupons(@PathVariable Long bookId){
        return couponIssueFeignClient.getDownloadableCoupons(bookId).getBody();
    }

    @PostMapping("/api-server/coupon-issue/issue")
    public Long issueCoupon(@RequestBody CouponIssueRequest couponIssueRequest){
        return couponIssueFeignClient.issueCoupon(couponIssueRequest).getBody();
    };
}
