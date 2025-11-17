package store.bookscamp.front.coupon.controller.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import store.bookscamp.front.coupon.type.DiscountType;
import store.bookscamp.front.coupon.type.TargetType;

public record CouponCreateRequest (
        @NotNull
        TargetType targetType,

        Long targetId,

        @NotNull
        DiscountType discountType,

        @NotNull
        @PositiveOrZero
        int discountValue,

        @NotNull
        @PositiveOrZero
        int minOrderAmount,

        @PositiveOrZero
        Integer maxDiscountAmount,

        @Positive
        Integer validDays,

        @NotNull
        String name
) {
}
