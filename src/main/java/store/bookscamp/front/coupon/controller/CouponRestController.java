package store.bookscamp.front.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import store.bookscamp.front.coupon.controller.request.CouponCreateRequest;
import store.bookscamp.front.coupon.feign.CouponFeignClient;

@RestController
@RequiredArgsConstructor
public class CouponRestController {

    private final CouponFeignClient couponFeignClient;

    @PostMapping("/api-server/admin/coupons")
    public void createCoupon(@RequestBody CouponCreateRequest request){
        couponFeignClient.createCoupon(request);
    }

    @PostMapping("/api-server/admin/coupons/{couponId}")
    public void deleteCoupon(@PathVariable Long couponId){
        couponFeignClient.deleteCoupon(couponId);
    }
}
