package store.bookscamp.front.couponissue.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.couponissue.controller.response.CouponIssueResponse;
import store.bookscamp.front.couponissue.controller.status.CouponFilterStatus;
import store.bookscamp.front.couponissue.feign.CouponIssueFeignClient;

@Controller
@RequiredArgsConstructor
public class CouponIssueController {

    private final CouponIssueFeignClient couponIssueFeignClient;

    @GetMapping("/mycoupon")
    public String getMyCoupons(
            Model model,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(name = "status", required = false, defaultValue = "ALL") CouponFilterStatus status
            ){

        if (userDetails == null) {

            return "redirect:/login";
        }

        ResponseEntity<List<CouponIssueResponse>> coupons = couponIssueFeignClient.getMyCoupons(status);

        model.addAttribute("coupons",coupons.getBody());

        return "couponissue/mycoupon";
    }
}
