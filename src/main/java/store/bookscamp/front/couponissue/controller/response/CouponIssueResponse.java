package store.bookscamp.front.couponissue.controller.response;

import java.time.LocalDateTime;

public record CouponIssueResponse(

        Long couponIssueId,
        String targetType,
        String discountType,
        Integer discountValue,
        Integer minOrderAmount,
        Integer maxDiscountAmount,
        LocalDateTime expiredAt,
        CouponIssueResponse status,
        LocalDateTime usedAt,
        String name
) {
}
