package store.bookscamp.front.order.dto;

import java.util.List;

public record OrderCreateRequest(
        List<OrderItemRequest> items,
        DeliveryInfo deliveryInfo,
        Long couponId,
        Integer usedPoint,
        NonMemberInfo nonMemberInfo
) {
    public record OrderItemRequest(
            Long bookId,
            Integer quantity,
            Long packagingId
    ) {}

    public record DeliveryInfo(
            String recipientName,
            String recipientPhone,
            Integer zipCode,
            String roadNameAddress,
            String detailAddress,
            String desiredDeliveryDate,
            String deliveryMemo
    ) {}

    public record NonMemberInfo(
            String password
    ) {}
}