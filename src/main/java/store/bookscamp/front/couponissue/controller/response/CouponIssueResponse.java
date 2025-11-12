package store.bookscamp.front.couponissue.controller.response;

import java.time.LocalDateTime;
import store.bookscamp.front.couponissue.controller.status.CouponIssueStatus;

public record CouponIssueResponse(
        Long couponIssueId,
        String targetType,
        String discountType,
        Integer discountValue,
        Integer minOrderAmount,
        Integer maxDiscountAmount,
        LocalDateTime expiredAt,
        CouponIssueStatus status,
        LocalDateTime usedAt,
        String name
) {
}
