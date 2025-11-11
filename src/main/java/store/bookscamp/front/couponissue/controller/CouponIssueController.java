package store.bookscamp.front.couponissue.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import store.bookscamp.front.couponissue.controller.response.CouponIssueResponse;
import store.bookscamp.front.couponissue.feign.CouponIssueFeignClient;

@Controller
@RequiredArgsConstructor
public class CouponIssueController {

    private final CouponIssueFeignClient couponIssueFeignClient;

    @GetMapping("/mycoupon")
    public String getMyCoupons(Model model){

        List<CouponIssueResponse> coupons = couponIssueFeignClient.getMyCoupons().getBody();
        model.addAttribute("coupons",coupons);

        return "couponissue/mycoupon";
    }
}
