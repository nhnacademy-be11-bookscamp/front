package store.bookscamp.front.coupon.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import store.bookscamp.front.coupon.feign.CouponFeignClient;

@Controller
@RequiredArgsConstructor
public class CouponController {

    private final CouponFeignClient couponFeignClient;

}
