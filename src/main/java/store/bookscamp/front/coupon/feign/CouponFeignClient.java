package store.bookscamp.front.coupon.feign;

import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import store.bookscamp.front.common.config.FeignConfig;
import store.bookscamp.front.coupon.controller.response.CouponResponse;

@FeignClient(
        name = "coupon",
        url = "${gateway.base-url}",
        configuration = FeignConfig.class
)
public interface CouponFeignClient {

    @PostMapping("/api-server/coupons")
    ResponseEntity<Void> createCoupon();

    @GetMapping("/api-server/coupons")
    ResponseEntity<List<CouponResponse>> listCoupons();

    @DeleteMapping("/api-server/coupons/{couponId}")
    ResponseEntity<Void> deleteCoupon(@PathVariable Long couponId);
}
