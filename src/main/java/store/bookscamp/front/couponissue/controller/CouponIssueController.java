package store.bookscamp.front.couponissue.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import store.bookscamp.front.common.pagination.RestPageImpl;
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
            @RequestParam(name = "status", required = false, defaultValue = "ALL") CouponFilterStatus status,
            @PageableDefault(size = 10, sort = "createdAt,desc") Pageable pageable
            ){

        if (userDetails == null) {

            return "redirect:/login";
        }

        ResponseEntity<RestPageImpl<CouponIssueResponse>> myCoupons = couponIssueFeignClient.getMyCoupons(status, pageable);
        RestPageImpl<CouponIssueResponse> couponPage = myCoupons.getBody();

        model.addAttribute("couponPage", couponPage);
        model.addAttribute("currentStatus",status);

        return "couponissue/mycoupon";
    }
}
