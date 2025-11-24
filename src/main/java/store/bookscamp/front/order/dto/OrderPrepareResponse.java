package store.bookscamp.front.order.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

public record OrderPrepareResponse(
        List<OrderItemInfo> orderItems,
        PriceInfo priceInfo,
        Integer availablePoint,
        List<PackagingInfo> availablePackagings,
        List<CouponInfo> availableCoupons
) {
    public record OrderItemInfo(
            Long bookId,
            String bookTitle,
            String bookImageUrl,
            Integer bookPrice,
            Integer quantity,
            Integer bookTotalAmount,
            Boolean packagingAvailable
    ) {}

    public record PriceInfo(
            Integer netAmount,
            Integer deliveryFee,
            Integer totalAmount,
            Integer freeDeliveryThreshold
    ) {}

    public record PackagingInfo(
            Long id,
            String name,
            Integer price
    ) {}

    public record CouponInfo(
            Long couponIssueId,
            Long couponId,
            String couponName,
            String discountType,
            Integer discountValue,
            Integer minOrderAmount,
            Integer maxDiscountAmount,
            Integer expectedDiscount
    ) {}
}