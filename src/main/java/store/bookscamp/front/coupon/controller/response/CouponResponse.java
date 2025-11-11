package store.bookscamp.front.coupon.controller.response;

import store.bookscamp.front.coupon.type.DiscountType;
import store.bookscamp.front.coupon.type.TargetType;

public record CouponResponse(

        Long id,
        TargetType targetType,
        Long targetId,
        DiscountType discountType,
        int discountValue,
        int minOrderAmount,
        Integer maxDiscountAmount,
        Integer validDays
) {
}
