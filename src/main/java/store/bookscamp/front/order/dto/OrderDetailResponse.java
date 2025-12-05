package store.bookscamp.front.order.dto;

import java.time.LocalDateTime;
import java.util.List;

public record OrderDetailResponse(
        Long orderId,
        LocalDateTime orderDate,
        String orderStatus,

        // 주문 상품 목록
        List<OrderDetailItemResponse> items,

        // 배송 정보
        String recipientName,
        String recipientPhone,
        String deliveryAddress,
        String deliveryMemo,

        int productAmount,
        int deliveryFee,
        int packagingFee,
        int discountAmount,
        int usedPoint,
        int finalPaymentAmount
) {
    public record OrderDetailItemResponse(
            Long bookId,
            String bookTitle,
            int orderQuantity,
            int bookPrice,
            int bookTotalAmount
    ) {
    }
}
