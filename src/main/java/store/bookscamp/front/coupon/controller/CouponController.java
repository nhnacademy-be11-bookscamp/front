package store.bookscamp.front.coupon.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import store.bookscamp.front.book.feign.BookFeignClient;
import store.bookscamp.front.coupon.controller.response.CouponResponse;
import store.bookscamp.front.coupon.feign.CouponFeignClient;

@Controller
@RequiredArgsConstructor
public class CouponController {

    private final BookFeignClient bookFeignClient;
    private final CouponFeignClient couponFeignClient;

    @GetMapping("/admin/coupons/new")
    public String listCoupons(Model model){

        List<CouponResponse> couponResponseList = couponFeignClient.listCoupons().getBody();

        model.addAttribute("coupons", couponResponseList);
        return "admin/coupon";
    }
}
